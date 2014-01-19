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
import android.view.Menu;

import com.grokkingandroid.sampleapp.samples.BaseConstants;
import com.grokkingandroid.sampleapp.samples.SampleBaseActivity;

import com.grokkingandroid.sampleapp.samples.gcm.R;


public class DescriptionActivity extends SampleBaseActivity {

   public static final String EXTRA_DESC_INSTANCE = "extraDescInstance";
   
   private int mLibTitlesId;
   private int mLibDescriptionsId;
   private Class mHomeActivityClass;
   private int mAppTitleId;
   private int mCopyrightYearId;
   private int mRepositoryLinkid;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      Bundle extras = getIntent().getExtras();
      int descId = extras.getInt(BaseConstants.KEY_DESCRIPTION_ID);
      int linkTextsId = extras.getInt(BaseConstants.KEY_LINK_TEXTS_ID);
      int linkTargetsId = extras.getInt(BaseConstants.KEY_LINK_TARGETS_ID);
      mLibTitlesId = extras.getInt(BaseConstants.KEY_LIB_TITLES_ID);
      mLibDescriptionsId = extras.getInt(BaseConstants.KEY_LIB_DESCRIPTIONS_ID);
      mHomeActivityClass = (Class)extras.getSerializable(BaseConstants.KEY_HOME_CLASS);
      mAppTitleId = extras.getInt(BaseConstants.KEY_APP_TITLE_ID);
      mRepositoryLinkid = extras.getInt(BaseConstants.KEY_REPOSITORY_LINK_ID);
      mCopyrightYearId = extras.getInt(BaseConstants.KEY_COPYRIGHT_YEAR_ID);
      setContentView(R.layout.activity_fragment_container);
      if (savedInstanceState == null) {
         DescriptionFragment fragment = DescriptionFragment.newInstance(descId, linkTextsId, linkTargetsId);
         getSupportFragmentManager()
               .beginTransaction()
               .add(R.id.demo_fragment_container, fragment)
               .commit();
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      boolean res = super.onCreateOptionsMenu(menu);
      menu.findItem(R.id.menu_description).setVisible(false);
      return res;
   }



   protected Class getSampleHomeActivity() {
      return mHomeActivityClass;
   }

   @Override
   protected int getDescriptionTextId() {
      return 0; // irrelevant
   }

   @Override
   protected int getLinkTextsArrayId() {
      return 0; // irrelevant
   }

   @Override
   protected int getLinkTargetsArrayId() {
      return 0; // irrelevant
   }

   @Override
   protected int getLibTitlesArrayId() {
      return mLibTitlesId;
   }

   @Override
   protected int getLibDescriptionsArrayId() {
      return mLibDescriptionsId;
   }

   @Override
   protected int getAppTitleResId() {
      return mAppTitleId;
   }

   @Override
   protected int getCopyrightYearResId() {
      return mCopyrightYearId;
   }

   @Override
   protected int getRepositoryLinkResId() {
      return mRepositoryLinkid;
   }
}
