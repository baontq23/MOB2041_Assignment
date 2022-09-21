package com.baontq.pnlib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baontq.pnlib.activity.LoginActivity;
import com.baontq.pnlib.fragment.BookManagementFragment;
import com.baontq.pnlib.fragment.CallCardManagementFragment;
import com.baontq.pnlib.fragment.ChangeInformationFragment;
import com.baontq.pnlib.fragment.CustomerManagementFragment;
import com.baontq.pnlib.fragment.GenreManagementFragment;
import com.baontq.pnlib.fragment.IncomeAnalyticsFragment;
import com.baontq.pnlib.fragment.LibrarianManagementFragment;
import com.baontq.pnlib.fragment.Top10SellerFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FrameLayout frameLayoutMainActivityContent;
    private NavigationView navigationViewMainActivityNavigateFragment;
    private TextView tvDrawerFullName, tvDrawerHeaderRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        //Toolbar
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white, getTheme()));
        replaceFragment(new IncomeAnalyticsFragment(), getString(R.string.menu_income));
        navigationViewMainActivityNavigateFragment.setNavigationItemSelectedListener(this);
        //navigationViewMainActivityNavigateFragment.getMenu().findItem(mNavItemId).setChecked(true);
        drawerToggle.syncState();
        tvDrawerFullName.setText(getSharedPreferences("session",Context.MODE_PRIVATE).getString(getString(R.string.session_data_fullname), "Full name"));
        tvDrawerHeaderRole.setText(getSharedPreferences("session", Context.MODE_PRIVATE).getString(getString(R.string.session_data_role), "Role"));

    }

    private void findView() {
        drawer = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar_main_title);
        frameLayoutMainActivityContent = findViewById(R.id.frameLayout_mainActivity_content);
        navigationViewMainActivityNavigateFragment = findViewById(R.id.navigationView_mainActivity_navigateFragment);
        tvDrawerHeaderRole = navigationViewMainActivityNavigateFragment.getHeaderView(0).findViewById(R.id.tv_drawer_header_role);
        tvDrawerFullName = navigationViewMainActivityNavigateFragment.getHeaderView(0).findViewById(R.id.tv_drawer_full_name);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_call_card_manage:
                replaceFragment(new CallCardManagementFragment(), getString(R.string.menu_call_card));
                break;
            case R.id.nav_book_genre_manage:
                replaceFragment(new GenreManagementFragment(), getString(R.string.menu_genre));
                break;
            case R.id.nav_book_manage:
                replaceFragment(new BookManagementFragment(), getString(R.string.menu_book));
                break;
            case R.id.nav_customer_manage:
                replaceFragment(new CustomerManagementFragment(), getString(R.string.menu_customer));
                break;
            case R.id.nav_top10_book:
                replaceFragment(new Top10SellerFragment(), getString(R.string.menu_top10));
                break;
            case R.id.nav_income:
                replaceFragment(new IncomeAnalyticsFragment(), getString(R.string.menu_income));
                break;
            case R.id.nav_librarian_manage:
                replaceFragment(new LibrarianManagementFragment(), getString(R.string.menu_librarian));
                break;
            case R.id.nav_change_password:
                replaceFragment(new ChangeInformationFragment(), getString(R.string.menu_change_info));
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        return true;
    }

    private void replaceFragment(Fragment fragment, String title) {
        getSupportActionBar().setTitle(title);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_mainActivity_content, fragment);
        transaction.commit();
    }

    private void logout() {
        getSharedPreferences("session", Context.MODE_PRIVATE).edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();

    }
}