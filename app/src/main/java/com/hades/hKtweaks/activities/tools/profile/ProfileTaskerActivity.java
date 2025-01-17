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
package com.hades.hKtweaks.activities.tools.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hades.hKtweaks.BuildConfig;
import com.hades.hKtweaks.R;
import com.hades.hKtweaks.activities.BaseActivity;
import com.hades.hKtweaks.database.tools.profiles.Profiles;
import com.hades.hKtweaks.fragments.tools.ProfileFragment;
import com.hades.hKtweaks.services.profile.Tasker;

import java.util.List;

import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;

/**
 * Created by willi on 21.07.16.
 */
public class ProfileTaskerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        ToolbarLayout toolbarLayout = getToolBarLayout();
        toolbarLayout.setTitle(getString(R.string.profile_select));
        toolbarLayout.setNavigationButtonOnClickListener(v -> onBackPressed());

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, getFragment(),
                "profile_fragment").commit();
    }

    private Fragment getFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("create_fragment");
        if (fragment == null) {
            return ProfileFragment.newInstance(true);
        }
        return fragment;
    }

    public void finish(String name, List<Profiles.ProfileItem.CommandItem> commands) {
        StringBuilder command = new StringBuilder();
        command.append(name).append(Tasker.DIVIDER);
        for (Profiles.ProfileItem.CommandItem commandItem : commands) {
            if (command.length() != 0) {
                command.append(Tasker.DIVIDER);
            }
            command.append(commandItem.getCommand());
        }

        Intent resultIntent = new Intent();
        Bundle resultBundle = new Bundle();
        resultBundle.putInt(Tasker.BUNDLE_EXTRA_INT_VERSION_CODE, BuildConfig.VERSION_CODE);
        resultBundle.putString(Tasker.BUNDLE_EXTRA_STRING_MESSAGE, command.toString());
        resultIntent.putExtra(Tasker.EXTRA_BUNDLE, resultBundle);
        resultIntent.putExtra(Tasker.EXTRA_STRING_BLURB, name);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void finish() {
        getSupportFragmentManager().beginTransaction().remove(getFragment()).commit();
        super.finish();
    }

}
