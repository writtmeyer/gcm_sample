/*
 * Copyright (C) 2014 Wolfram Rittmeyer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grokkingandroid.sampleapp.samples.description;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grokkingandroid.sampleapp.samples.BaseConstants;
import com.grokkingandroid.sampleapp.samples.gcm.R;


public class DescriptionFragment extends Fragment {
   
   private DescriptionDelegate mDescDelegate;

   public static DescriptionFragment newInstance(int descriptionId,
         int linkTextsId, int linkTargetsId) {
      DescriptionFragment f = new DescriptionFragment();
      Bundle bundle = new Bundle();
      bundle.putInt(BaseConstants.KEY_LINK_TARGETS_ID, linkTargetsId);
      bundle.putInt(BaseConstants.KEY_LINK_TEXTS_ID, linkTextsId);
      bundle.putInt(BaseConstants.KEY_DESCRIPTION_ID, descriptionId);
      f.setArguments(bundle);
      return f;
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {      
      ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_demo_description,
            container, false);
      mDescDelegate = new DescriptionDelegate();
      mDescDelegate.addDescriptionAndLinks(inflater, 
            rootView, 
            getResources(), 
            getArguments().getInt(BaseConstants.KEY_DESCRIPTION_ID),
            getArguments().getInt(BaseConstants.KEY_LINK_TEXTS_ID),
            getArguments().getInt(BaseConstants.KEY_LINK_TARGETS_ID)
            );
      return rootView;
   }

   @Override
   public void onResume() {
      super.onResume();
      if (mDescDelegate != null) {
         mDescDelegate.clearFocusOnLinks();
      }
   }
   
   

}
