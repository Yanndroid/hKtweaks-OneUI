/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.hades.hKtweaks.utils.kernel.cpu;

import android.content.Context;

import com.hades.hKtweaks.fragments.ApplyOnBootFragment;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.root.Control;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 05.05.16.
 */
public class CPUBoost {

    private static final String CPU_BOOST = "/sys/module/cpu_boost/parameters";
    private static final String CPU_BOOST_EXYNOS = "/sys/kernel/cpu_input_boost";
    private static final List<String> sEnable = new ArrayList<>();
    private static final String CPU_BOOST_DEBUG_MASK = CPU_BOOST + "/debug_mask";
    private static final String CPU_BOOST_MS = CPU_BOOST + "/boost_ms";
    private static final String CPU_BOOST_SYNC_THRESHOLD = CPU_BOOST + "/sync_threshold";
    private static final String CPU_BOOST_INPUT_MS = CPU_BOOST + "/input_boost_ms";
    private static final String CPU_BOOST_INPUT_BOOST_FREQ = CPU_BOOST + "/input_boost_freq";
    private static final String CPU_BOOST_WAKEUP = CPU_BOOST + "/wakeup_boost";
    private static final String CPU_BOOST_HOTPLUG = CPU_BOOST + "/hotplug_boost";
    private static final String CPU_BOOST_EXYNOS_INPUT_MS = CPU_BOOST_EXYNOS + "/ib_duration_ms";
    private static final String CPU_BOOST_EXYNOS_BOOST_FREQ = CPU_BOOST_EXYNOS + "/ib_freqs";
    private static CPUBoost sInstance;

    static {
        sEnable.add(CPU_BOOST + "/cpu_boost");
        sEnable.add(CPU_BOOST + "/cpuboost_enable");
        sEnable.add(CPU_BOOST + "/input_boost_enabled");
        sEnable.add(CPU_BOOST_EXYNOS + "/enabled");
    }

    private String ENABLE;

    private CPUBoost() {
        for (String file : sEnable) {
            if (Utils.existFile(file)) {
                ENABLE = file;
                break;
            }
        }
    }

    public static CPUBoost getInstance() {
        if (sInstance == null) {
            sInstance = new CPUBoost();
        }
        return sInstance;
    }

    public void setCpuBoostExynosInputFreq(String value1, String value2, Context context) {
        String value = value1 + " " + value2;
        run(Control.write(String.valueOf(value), CPU_BOOST_EXYNOS_BOOST_FREQ), CPU_BOOST_EXYNOS_BOOST_FREQ, context);
    }

    public List<String> getCpuBootExynosInputFreq() {
        String freqs[] = Utils.readFile(CPU_BOOST_EXYNOS_BOOST_FREQ).split(" ");
        List<String> INPUT_FREQS = new ArrayList<>();
        for (String freq : freqs) {
            INPUT_FREQS.add(freq.trim());
        }
        return INPUT_FREQS;
    }

    public boolean hasCpuBoostExynosInputFreq() {
        return Utils.existFile(CPU_BOOST_EXYNOS_BOOST_FREQ);
    }

    public boolean hasCpuBoostExynosInputMs() {
        return Utils.existFile(CPU_BOOST_EXYNOS_INPUT_MS);
    }

    public void setCpuBoostExynosInputMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_EXYNOS_INPUT_MS), CPU_BOOST_EXYNOS_INPUT_MS, context);
    }

    public String getCpuBootExynosInputMs() {
        return Utils.readFile(CPU_BOOST_EXYNOS_INPUT_MS);
    }

    public void enableCpuBoostWakeup(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", CPU_BOOST_WAKEUP), CPU_BOOST_WAKEUP, context);
    }

    public boolean isCpuBoostWakeupEnabled() {
        return Utils.readFile(CPU_BOOST_WAKEUP).equals("Y");
    }

    public boolean hasCpuBoostWakeup() {
        return Utils.existFile(CPU_BOOST_WAKEUP);
    }

    public void enableCpuBoostHotplug(boolean enable, Context context) {
        run(Control.write(enable ? "Y" : "N", CPU_BOOST_HOTPLUG), CPU_BOOST_HOTPLUG, context);
    }

    public boolean isCpuBoostHotplugEnabled() {
        return Utils.readFile(CPU_BOOST_HOTPLUG).equals("Y");
    }

    public boolean hasCpuBoostHotplug() {
        return Utils.existFile(CPU_BOOST_HOTPLUG);
    }

    public void setCpuBoostInputMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_INPUT_MS), CPU_BOOST_INPUT_MS, context);
    }

    public int getCpuBootInputMs() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_INPUT_MS));
    }

    public boolean hasCpuBoostInputMs() {
        return Utils.existFile(CPU_BOOST_INPUT_MS);
    }

    public void setCpuBoostInputFreq(int value, int core, Context context) {
        if (Utils.readFile(CPU_BOOST_INPUT_BOOST_FREQ).contains(":")) {
            run(Control.write(core + ":" + value, CPU_BOOST_INPUT_BOOST_FREQ),
                    CPU_BOOST_INPUT_BOOST_FREQ + core, context);
        } else {
            run(Control.write(String.valueOf(value), CPU_BOOST_INPUT_BOOST_FREQ),
                    CPU_BOOST_INPUT_BOOST_FREQ, context);
        }
    }

    public List<Integer> getCpuBootInputFreq() {
        CPUFreq cpuFreq = CPUFreq.getInstance();

        List<Integer> list = new ArrayList<>();
        String value = Utils.readFile(CPU_BOOST_INPUT_BOOST_FREQ);
        if (value.contains(":")) {
            for (String line : value.split(" ")) {
                int core = Utils.strToInt(line.split(":")[0]);
                String freq = line.split(":")[1];
                try {
                    list.add(freq.equals("0") ? 0 : cpuFreq.getFreqs(core).indexOf(Utils.strToInt(freq)) + 1);
                } catch (NullPointerException ignored) {
                }
            }
        } else {
            list.add(value.equals("0") ? 0 : cpuFreq.getFreqs().indexOf(Utils.strToInt(value)) + 1);
        }
        return list;
    }

    public boolean hasCpuBoostInputFreq() {
        return Utils.existFile(CPU_BOOST_INPUT_BOOST_FREQ);
    }

    public void setCpuBoostSyncThreshold(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_SYNC_THRESHOLD), CPU_BOOST_SYNC_THRESHOLD, context);
    }

    public int getCpuBootSyncThreshold() {
        return CPUFreq.getInstance().getFreqs().indexOf(Utils.strToInt(Utils.readFile(CPU_BOOST_SYNC_THRESHOLD))) + 1;
    }

    public boolean hasCpuBoostSyncThreshold() {
        return Utils.existFile(CPU_BOOST_SYNC_THRESHOLD);
    }

    public void setCpuBoostMs(int value, Context context) {
        run(Control.write(String.valueOf(value), CPU_BOOST_MS), CPU_BOOST_MS, context);
    }

    public int getCpuBootMs() {
        return Utils.strToInt(Utils.readFile(CPU_BOOST_MS));
    }

    public boolean hasCpuBoostMs() {
        return Utils.existFile(CPU_BOOST_MS);
    }

    public void enableCpuBoostDebugMask(boolean enable, Context context) {
        run(Control.write(enable ? "1" : "0", CPU_BOOST_DEBUG_MASK), CPU_BOOST_DEBUG_MASK, context);
    }

    public boolean isCpuBoostDebugMaskEnabled() {
        return Utils.readFile(CPU_BOOST_DEBUG_MASK).equals("1");
    }

    public boolean hasCpuBoostDebugMask() {
        return Utils.existFile(CPU_BOOST_DEBUG_MASK);
    }

    public void enableCpuBoost(boolean enable, Context context) {
        run(Control.write(
                ENABLE.endsWith("cpuboost_enable") ? (enable ? "Y" : "N") : (enable ? "1" : "0"), ENABLE),
                ENABLE, context);
    }

    public boolean isEnabled() {
        String value = Utils.readFile(ENABLE);
        return value.equals("1") || value.equals("Y");
    }

    public boolean hasEnable() {
        return ENABLE != null;
    }

    public boolean supported() {
        return hasEnable() || hasCpuBoostDebugMask() || hasCpuBoostMs() || hasCpuBoostSyncThreshold()
                || hasCpuBoostInputFreq() || hasCpuBoostInputMs() || hasCpuBoostHotplug() || hasCpuBoostWakeup()
                || hasCpuBoostExynosInputFreq() || hasCpuBoostExynosInputMs();
    }

    private void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU, id, context);
    }

}
