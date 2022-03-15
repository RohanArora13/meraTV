/*******************************************************************************
 * Copyright (C) Rohan Arora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Rohan Arora <rohanarora1313@gmail.com> on 23/1/19 9:39 PM
 *
 * Last modified 23/1/19 9:39 PM
 *
 ******************************************************************************/

package channel.price.calculator.meratv;

import android.os.Bundle;
//import android.support.annotation.NonNull;
//import androidx.core.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class view_selectionFragment extends DialogFragment {

    public view_selectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }
}
