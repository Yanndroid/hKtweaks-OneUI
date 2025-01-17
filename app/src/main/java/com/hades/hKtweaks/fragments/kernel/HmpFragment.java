package com.hades.hKtweaks.fragments.kernel;

import com.hades.hKtweaks.R;
import com.hades.hKtweaks.activities.tools.profile.ProfileActivity;
import com.hades.hKtweaks.fragments.recyclerview.RecyclerViewFragment;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.kernel.hmp.Hmp;
import com.hades.hKtweaks.views.recyclerview.ApplyOnBootFView;
import com.hades.hKtweaks.views.recyclerview.CardView;
import com.hades.hKtweaks.views.recyclerview.DescriptionView;
import com.hades.hKtweaks.views.recyclerview.RecyclerViewItem;
import com.hades.hKtweaks.views.recyclerview.SeekBarView;
import com.hades.hKtweaks.views.recyclerview.TitleView;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by MoroGoku on 10/10/2017.
 */

public class HmpFragment extends RecyclerViewFragment {

    private final LinkedHashMap<Integer, String> sProfiles = new LinkedHashMap<>();
    private Hmp mHmp;
    private SeekBarView mUpThreshold;
    private SeekBarView mDownThreshold;

    @Override
    protected void init() {
        super.init();

        sProfiles.clear();
        sProfiles.put(R.string.stock, "524 214");
        sProfiles.put(R.string.battery, "700 256");
        sProfiles.put(R.string.performance, "430 150");
        mHmp = Hmp.getInstance();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (!(getActivity() instanceof ProfileActivity))
            items.add(new ApplyOnBootFView(getActivity(), this));

        CardView card = new CardView(getActivity());
        card.setTitle(getString(R.string.hmp_long));

        DescriptionView hmp = new DescriptionView();
        hmp.setSummary(getString(R.string.hmp_desc));
        card.addItem(hmp);

        if (mHmp.hasUpThreshold()) {
            mUpThreshold = new SeekBarView();
            mUpThreshold.setTitle(getString(R.string.hmp_up_threshold));
            mUpThreshold.setSummary(getString(R.string.hmp_up_threshold_summary));
            mUpThreshold.setMax(1024);
            mUpThreshold.setMin(1);
            mUpThreshold.setProgress(Utils.strToInt(mHmp.getUpThreshold()) - 1);
            mUpThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mHmp.setUpThreshold((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            card.addItem(mUpThreshold);
        }

        if (mHmp.hasDownThreshold()) {
            mDownThreshold = new SeekBarView();
            mDownThreshold.setTitle(getString(R.string.hmp_down_threshold));
            mDownThreshold.setSummary(getString(R.string.hmp_down_threshold_summary));
            mDownThreshold.setMax(1024);
            mDownThreshold.setMin(1);
            mDownThreshold.setProgress(Utils.strToInt(mHmp.getDownThreshold()) - 1);
            mDownThreshold.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                @Override
                public void onStop(SeekBarView seekBarView, int position, String value) {
                    mHmp.setDownThreshold((position + 1), getActivity());
                }

                @Override
                public void onMove(SeekBarView seekBarView, int position, String value) {
                }
            });

            card.addItem(mDownThreshold);
        }

        if (card.size() > 0) {
            items.add(card);
        }

        if (mHmp.hasUpThreshold() && mHmp.hasDownThreshold()) {

            TitleView profilesTitle = new TitleView();
            profilesTitle.setText(getString(R.string.profile));
            items.add(profilesTitle);

            for (int id : sProfiles.keySet()) {
                DescriptionView profile = new DescriptionView();
                profile.setTitle(getString(id));
                profile.setSummary(sProfiles.get(id));
                profile.setOnItemClickListener((item) -> {
                    mHmp.setHmpProfile(((DescriptionView) item).getSummary().toString(), getActivity());
                    refreshHmpProfile();
                });

                items.add(profile);
            }
        }
    }

    private void refreshHmpProfile() {
        getHandler().postDelayed(() -> {
            mUpThreshold.setProgress(Utils.strToInt(mHmp.getUpThreshold()) - 1);
            mDownThreshold.setProgress(Utils.strToInt(mHmp.getDownThreshold()) - 1);
        }, 250);
    }
}
