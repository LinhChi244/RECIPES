package com.example.recipes.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.recipes.adapters.RecipeAdapter;
import com.example.recipes.databinding.FragmentFavouritesBinding;
import com.example.recipes.models.FavouriteRecipe;
import com.example.recipes.models.Recipe;
import com.example.recipes.room.RecipeRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {
    FragmentFavouritesBinding binding;
    RecipeRepository recipeRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        recipeRepository = new RecipeRepository(requireActivity().getApplication());
        List<FavouriteRecipe> favouriteRecipes = recipeRepository.getAllFavourites();
        if (favouriteRecipes.isEmpty()) {
            Toast.makeText(requireContext(), "No Favourites", Toast.LENGTH_SHORT).show();
            binding.rvFavourites.setVisibility(View.GONE);
            binding.noFavourites.setVisibility(View.VISIBLE);
        } else {
            binding.rvFavourites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            binding.rvFavourites.setAdapter(new RecipeAdapter());
            List<Recipe> recipes = new ArrayList<>();
            DatabaseReference reference = FirebaseDatabase.getInstance("https://recipes-71464-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Recipes");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            for (FavouriteRecipe favouriteRecipe : favouriteRecipes) {
                                if (dataSnapshot.getKey().equals(favouriteRecipe.getRecipeId())) {
                                    recipes.add(dataSnapshot.getValue(Recipe.class));
                                }
                            }
                        }
                        binding.rvFavourites.setVisibility(View.VISIBLE);
                        binding.noFavourites.setVisibility(View.GONE);
                        RecipeAdapter adapter = (RecipeAdapter) binding.rvFavourites.getAdapter();
                        if (adapter != null) {
                            adapter.setRecipeList(recipes);
                        }

                    } else {
                        binding.noFavourites.setVisibility(View.VISIBLE);
                        binding.rvFavourites.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FavouritesFragment", "onCancelled: " + error.getMessage());
                }
            });
        }
    }
}