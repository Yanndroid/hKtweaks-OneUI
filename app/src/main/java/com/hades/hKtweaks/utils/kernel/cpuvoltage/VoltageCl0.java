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
package com.hades.hKtweaks.utils.kernel.cpuvoltage;

import android.content.Context;

import com.hades.hKtweaks.fragments.ApplyOnBootFragment;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.root.Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 07.05.16.
 */
public class VoltageCl0 {

    public static final String BACKUP = "/data/.hKtweaks/cpuCl0_stock_voltage";

    public static final String CL0_VOLTAGE = "/sys/devices/system/cpu/cpufreq/mp-cpufreq/cluster0_volt_table";

    private static final HashMap<String, Boolean> sVoltages = new HashMap<>();
    private static final HashMap<String, Integer> sOffset = new HashMap<>();
    private static final HashMap<String, Integer> sOffsetFreq = new HashMap<>();
    private static final HashMap<String, String> sSplitNewline = new HashMap<>();
    private static final HashMap<String, String> sSplitLine = new HashMap<>();
    private static final HashMap<String, Boolean> sAppend = new HashMap<>();
    private static String PATH;
    private static String[] sFreqs;

    static {
        sVoltages.put(CL0_VOLTAGE, false);

        sOffsetFreq.put(CL0_VOLTAGE, 1000);

        sOffset.put(CL0_VOLTAGE, 1000);

        sSplitNewline.put(CL0_VOLTAGE, "\\r?\\n");

        sSplitLine.put(CL0_VOLTAGE, " ");

        sAppend.put(CL0_VOLTAGE, false);
    }

    public static void setVoltage(String freq, String voltage, Context context) {
        int position = getFreqs().indexOf(freq);
        if (sAppend.get(PATH)) {
            String command = "";
            List<String> voltages = getVoltages();
            for (int i = 0; i < voltages.size(); i++) {
                if (i == position) {
                    command += command.isEmpty() ? voltage : " " + voltage;
                } else {
                    command += command.isEmpty() ? voltages.get(i) : " " + voltages.get(i);
                }
            }
            run(Control.write(command, PATH), PATH, context);
        } else {
            freq = String.valueOf(Utils.strToInt(freq) * sOffsetFreq.get(PATH));
            String volt = String.valueOf((int) (Utils.strToFloat(voltage) * sOffset.get(PATH)));
            run(Control.write(freq + " " + volt, PATH), PATH + freq, context);
        }

    }

    public static int getOffset() {
        return sOffset.get(PATH);
    }

    public static List<String> getStockVoltages() {
        String value = Utils.readFile(BACKUP);
        if (!value.isEmpty()) {
            String[] lines = value.split(sSplitNewline.get(PATH));
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(sSplitLine.get(PATH));
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToFloat(voltageLine[1].trim()) / sOffset.get(PATH)));
                }
            }
            return voltages;
        }
        return null;
    }

    public static List<String> getVoltages() {
        String value = Utils.readFile(PATH);
        if (!value.isEmpty()) {
            String[] lines = value.split(sSplitNewline.get(PATH));
            List<String> voltages = new ArrayList<>();
            for (String line : lines) {
                String[] voltageLine = line.split(sSplitLine.get(PATH));
                if (voltageLine.length > 1) {
                    voltages.add(String.valueOf(Utils.strToFloat(voltageLine[1].trim()) / sOffset.get(PATH)));
                }
            }
            return voltages;
        }
        return null;
    }

    public static List<String> getFreqs() {
        if (sFreqs == null) {
            String value = Utils.readFile(PATH);
            if (!value.isEmpty()) {
                String[] lines = value.split(sSplitNewline.get(PATH));
                sFreqs = new String[lines.length];
                for (int i = 0; i < sFreqs.length; i++) {
                    sFreqs[i] = String.valueOf(Utils.strToInt(lines[i]
                            .split(sSplitLine.get(PATH))[0].trim()) / sOffsetFreq.get(PATH));
                }
            }
        }
        if (sFreqs == null) return null;
        return Arrays.asList(sFreqs);
    }

    public static boolean supported() {
        if (PATH != null) return true;
        for (String path : sVoltages.keySet()) {
            if (Utils.existFile(path)) {
                PATH = path;
            }
        }
        return PATH != null;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.CPU_CL0_VOLTAGE, id, context);
    }

}
