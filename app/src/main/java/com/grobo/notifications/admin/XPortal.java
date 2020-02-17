package com.grobo.notifications.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.admin.positions.CoordinatorFragment;
import com.grobo.notifications.admin.positions.MaintenanceSecretaryFragment;
import com.grobo.notifications.admin.positions.SubCoordintorFragment;
import com.grobo.notifications.admin.positions.TechnicalSecretaryFragment;
import com.grobo.notifications.admin.positions.VPFragment;
import com.grobo.notifications.utils.utils;

public class XPortal extends AppCompatActivity {

    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xportal);

        manager = getSupportFragmentManager();

        Intent i = getIntent();

        if (i.hasExtra("por")) {
            PORItem j = i.getParcelableExtra("por");
            if (j != null) setBaseFragment(j);
            else utils.showFinishAlertDialog(this, "Alert!!!", "Invalid POR!");

        } else {
            utils.showFinishAlertDialog(this, "Alert!!!", "Intent extra error!");
            finish();
        }
    }

    private void setBaseFragment(PORItem porItem) {
        if (findViewById(R.id.frame_layout_admin) != null) {

            Bundle b = new Bundle();
            b.putParcelable("por", porItem);

            Fragment f = null;

            switch (porItem.getCode()) {

                case 10:    //
                    f = new CoordinatorFragment();
                    break;

                case 20:    //
                    f = new SubCoordintorFragment();
                    break;

                case 30:    //
                    f = new SubCoordintorFragment();
                    break;
                case 31:    //sub-coordinator
                    f = new SubCoordintorFragment();
                    break;
                case 32:    //sub-lead
                    f = new SubCoordintorFragment();
                    break;

                case 40:    //
                    f = new CoordinatorFragment();
                    break;
                case 41:    //coordinator
                    f = new CoordinatorFragment();
                    break;
                case 42:    //lead
                    f = new CoordinatorFragment();
                    break;

                case 50:    //overall-coordinator
                    f = new CoordinatorFragment();
                    break;

                case 60:    //
                    f = new TechnicalSecretaryFragment();
                    break;
                case 61:    //tech
                    f = new TechnicalSecretaryFragment();
                    break;
                case 62:    //cult
                    f = new TechnicalSecretaryFragment();
                    break;
                case 63:    //sports
                    f = new TechnicalSecretaryFragment();
                    break;
                case 64:    //welfare
                    f = new MaintenanceSecretaryFragment();
                    break;
                case 65:    //maintenance
                    f = new MaintenanceSecretaryFragment();
                    break;
                case 66:    //mess
                    f = new MaintenanceSecretaryFragment();
                    break;

                case 70:    //
                    f = new TechnicalSecretaryFragment();
                    break;
                case 71:    //gen-tech
                    f = new TechnicalSecretaryFragment();
                    break;
                case 72:    //gen-cult
                    f = new TechnicalSecretaryFragment();
                    break;
                case 73:    //gen-sports
                    f = new TechnicalSecretaryFragment();
                    break;

                case 80:    //vp-gymkhana
                    f = new VPFragment();
                    break;

                case 90:    //superuser
                    f = new TechnicalSecretaryFragment();
                    break;

            }

            if (f != null) {
                f.setArguments(b);
                manager.beginTransaction().add(R.id.frame_layout_admin, f).commit();
            } else utils.showFinishAlertDialog(this, "Alert!!!", "Invalid POR!");

        }
    }

    private void transactFragment(Fragment frag) {
        manager.beginTransaction()
                .setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out)
                .replace(R.id.frame_layout_admin, frag)
                .addToBackStack(frag.getTag())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}