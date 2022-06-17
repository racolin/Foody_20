package hcmute.spkt.group20.foody_20.state_fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import hcmute.spkt.group20.foody_20.fragment.MealFragment;

public class HomeStateFragment extends FragmentStateAdapter {

    String key;

    public HomeStateFragment(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        key = "";
    }
    public HomeStateFragment(@NonNull FragmentActivity fragmentActivity, String key) {
        super(fragmentActivity);
        this.key = key;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new MealFragment(position, key);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}