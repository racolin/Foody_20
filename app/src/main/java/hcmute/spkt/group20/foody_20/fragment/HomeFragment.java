package hcmute.spkt.group20.foody_20.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hcmute.spkt.group20.foody_20.R;
import hcmute.spkt.group20.foody_20.adapter.SliderAdapter;
import hcmute.spkt.group20.foody_20.model.Meal;
import hcmute.spkt.group20.foody_20.model.Shop;
import hcmute.spkt.group20.foody_20.model.Slider;
import hcmute.spkt.group20.foody_20.state_fragment.HomeStateFragment;

public class HomeFragment extends Fragment {
    List<Meal> near, outstanding;
    ImageView iv_search;
    ViewPager2 vp2_home;
    TabLayout tl_tab;
    ViewPager2 vp2_slider;
    SliderAdapter adapterSlider;
    HomeStateFragment homeStateFragment;
    ArrayAdapter<String> adapter;
    Spinner sp_provinces;
    List<Slider> sliders;
    EditText et_search;

    StorageReference reference;
    FirebaseFirestore db;

    Handler handler;

    public HomeFragment() {
        reference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (vp2_slider.getCurrentItem() == vp2_slider.getAdapter().getItemCount() - 1) {
                vp2_slider.setCurrentItem(0);
            } else {
                vp2_slider.setCurrentItem(vp2_slider.getCurrentItem() + 1);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        et_search = view.findViewById(R.id.et_search);
        iv_search = view.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(v -> {
            homeStateFragment = new HomeStateFragment(getActivity(), et_search.getText().toString());
            vp2_home.setAdapter(homeStateFragment);
        });

        handler = new Handler();

        sliders = new ArrayList<>();
        vp2_slider = view.findViewById(R.id.vp2_slider);
        adapterSlider = new SliderAdapter(sliders);
        vp2_slider.setAdapter(adapterSlider);
        getSliders();
        vp2_slider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 4000);
            }
        });

        vp2_home = view.findViewById(R.id.vp2_home);
        near = new ArrayList<>();
        outstanding = new ArrayList<>();
        homeStateFragment = new HomeStateFragment(getActivity());
        vp2_home.setAdapter(homeStateFragment);

        tl_tab = view.findViewById(R.id.tl_tab);
        new TabLayoutMediator(tl_tab, vp2_home, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.outstanding);
                        break;
                    case 1:
                        tab.setText(R.string.near_me);
                        break;
                }
            }
        }).attach();

        sp_provinces = view.findViewById(R.id.sp_provinces);
        getProvinces();

        return view;
    }

    public void getSliders() {
        db.collection("sliders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                new Thread(() -> {
                                    try {
                                        Bitmap bm = Picasso.get()
                                                .load("http://10.0.140.232/foody/" + snapshot.getData().get("src").toString())
                                                .get();
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
                                        byte[] byteArray = stream.toByteArray();
                                        sliders.add(new Slider(byteArray));
                                        handler.post(() -> {
                                            adapterSlider.notifyDataSetChanged();
                                        });

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }).start();
//
//                                String img = snapshot.getData().get("src").toString();
//                                StorageReference image = reference.child(img);
//                                image.getBytes(1024 * 1024)
//                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                                            @Override
//                                            public void onSuccess(byte[] bytes) {
//                                                sliders.add(new Slider(bytes));
//                                                adapterSlider.notifyDataSetChanged();
//                                            }
//                                        });
                            }
                        }
                    }
                });
    }

    public void getProvinces() {
        List<String> provinces = new ArrayList<>();
        db.collection("provinces")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                provinces.add(snapshot.getData().get("name").toString());
                            }
                            adapter = new ArrayAdapter<String>(getContext(), R.layout.province_selected, R.id.tv_province, provinces);
                            sp_provinces.setAdapter(adapter);
                            adapter.setDropDownViewResource(R.layout.province_dropdown);
                        } else {

                        }
                    }
                });
    }
}
