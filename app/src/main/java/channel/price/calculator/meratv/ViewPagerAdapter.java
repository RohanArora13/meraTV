/*******************************************************************************
 * Copyright (C) Rohan Arora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Rohan Arora <rohanarora1313@gmail.com> on 30/12/18 3:34 PM
 *
 * Last modified 30/12/18 3:34 PM
 *
 ******************************************************************************/

package channel.price.calculator.meratv;


//import androidx.core.app.Fragment;
//import androidx.core.app.FragmentManager;
//import androidx.core.app.FragmentPagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import channel.price.calculator.meratv.selection.SelectionFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new MeFragment();
        }
        else if (position == 1)
        {
            fragment = new SelectionFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Channels";
        }
        else if (position == 1)
        {
            title = "Selections";
        }
        return title;
    }
}
