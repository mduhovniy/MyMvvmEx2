package info.duhovniy.mymvvmex.view;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import info.duhovniy.mymvvmex.R;
import info.duhovniy.mymvvmex.databinding.ItemRepoBinding;
import info.duhovniy.mymvvmex.model.Repo;
import info.duhovniy.mymvvmex.viewmodel.ItemRepoViewModel;

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder> {


    private List<Repo> repositories;

    public RepositoryAdapter() {
        this.repositories = Collections.emptyList();
    }

    public void setRepositories(List<Repo> repositories) {
        if(repositories != null) {
            this.repositories = repositories;
            notifyDataSetChanged();
        }
    }

    @Override
    public RepositoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemRepoBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_repo,
                parent,
                false);
        return new RepositoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RepositoryViewHolder holder, int position) {
        holder.bindRepository(repositories.get(position));
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }

    static class RepositoryViewHolder extends RecyclerView.ViewHolder {
        private ItemRepoBinding binding;    // changed from "final"!

        RepositoryViewHolder(ItemRepoBinding binding) {
            super(binding.cardView);
            this.binding = binding;
        }

        void bindRepository(Repo repository) {
            if (binding.getViewModel() == null) {
                binding.setViewModel(new ItemRepoViewModel(itemView.getContext(), repository));
            } else {
                binding.getViewModel().setRepository(repository);
            }
        }
    }
}
