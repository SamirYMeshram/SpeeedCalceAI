package com.yourname.speedcalcai.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yourname.speedcalcai.R;
import com.yourname.speedcalcai.adapters.RevisionAdapter;
import java.util.ArrayList;

public class RevisionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_revision, container, false);
        RecyclerView recycler = view.findViewById(R.id.recyclerRevision);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(new RevisionAdapter(items()));
        return view;
    }

    private ArrayList<RevisionAdapter.RevisionItem> items() {
        ArrayList<RevisionAdapter.RevisionItem> list = new ArrayList<>();
        list.add(new RevisionAdapter.RevisionItem("Addition Trick: Make 10 / 100", "Look for pairs: 7+3, 8+2, 48+52. Example: 28 + 72 + 16 = 116."));
        list.add(new RevisionAdapter.RevisionItem("Multiplication by 11", "For two digits AB: A+B goes in the middle. 43 x 11 = 473."));
        list.add(new RevisionAdapter.RevisionItem("Percentage", "x% of y = y% of x. 16% of 25 = 25% of 16 = 4."));
        list.add(new RevisionAdapter.RevisionItem("Squares near 100", "98^2 = (100-2)^2 = 10000 - 400 + 4 = 9604."));
        list.add(new RevisionAdapter.RevisionItem("Speed Time Work", "If A finishes in x days, one day work = 1/x."));
        list.add(new RevisionAdapter.RevisionItem("Pythagorean Triplets", "Common triplets: 3-4-5, 5-12-13, 7-24-25, 8-15-17."));
        return list;
    }
}
