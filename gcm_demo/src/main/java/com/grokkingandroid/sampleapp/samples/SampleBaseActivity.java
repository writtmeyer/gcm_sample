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
package com.grokkingandroid.sampleapp.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.grokkingandroid.sampleapp.samples.about.AboutFragment;
import com.grokkingandroid.sampleapp.samples.description.DescriptionActivity;
import com.grokkingandroid.sampleapp.samples.gcm.R;

/**
 * The base for all sample activities. Samples have to create a subclass
 * which provides the sample specific menu (the very least every sample
 * app has to provide is an about and a description menu item).
 *
 * @author Wolfram Rittmeyer
 *
 */
public abstract class SampleBaseActivity extends ActionBarActivity {
   
   private SampleBaseFragment mDemoFragment;
   
   @Override
   protected void onCreate(Bundle icicle) {
      super.onCreate(icicle);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_basesampleactivity, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int itemId = item.getItemId();
      if (itemId == android.R.id.home) {
         ActionBar ab = getSupportActionBar();
         if (ab != null && 
               (ab.getDisplayOptions() & ActionBar.DISPLAY_SHOW_HOME) == ActionBar.DISPLAY_SHOW_HOME &&
               (ab.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) == ActionBar.DISPLAY_HOME_AS_UP) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // Keep in mind: The Activity may, or may not
            // have an the up navigation enabled
            NavUtils.navigateUpTo(this,
                  new Intent(this, getSampleHomeActivity()));
         }
         return true;
      } else if (itemId == R.id.about) {
         showAboutDialog();
         return true;
      } else if (itemId == R.id.menu_description) {
         showDescription();
         return true;
      }
      return super.onOptionsItemSelected(item);
   }

   private void showAboutDialog() {
      int[] resIds = new int[]{
            getLibTitlesArrayId(), 
            getLibDescriptionsArrayId(), 
            getAboutTextId(),
            getAppTitleResId(),
            getCopyrightYearResId(),
            getRepositoryLinkResId()};
      DialogFragment newFragment = AboutFragment.newInstance(resIds, getAddDefaultLibs());
      newFragment.show(getSupportFragmentManager(), "dialog");
   }
   
   private void showDescription() {
      Intent descIntent = new Intent(this, DescriptionActivity.class);
      descIntent.putExtra(BaseConstants.KEY_DESCRIPTION_ID, getDescriptionTextId());
      descIntent.putExtra(BaseConstants.KEY_LINK_TEXTS_ID, getLinkTextsArrayId());
      descIntent.putExtra(BaseConstants.KEY_LINK_TARGETS_ID, getLinkTextsArrayId());
      descIntent.putExtra(BaseConstants.KEY_HOME_CLASS, getSampleHomeActivity());
      descIntent.putExtra(BaseConstants.KEY_LIB_TITLES_ID, getLibTitlesArrayId());
      descIntent.putExtra(BaseConstants.KEY_LIB_DESCRIPTIONS_ID, getLibDescriptionsArrayId());
      descIntent.putExtra(BaseConstants.KEY_APP_TITLE_ID, getAppTitleResId());
      descIntent.putExtra(BaseConstants.KEY_REPOSITORY_LINK_ID, getRepositoryLinkResId());
      descIntent.putExtra(BaseConstants.KEY_COPYRIGHT_YEAR_ID, getCopyrightYearResId());
      startActivity(descIntent);
   }
   
   protected abstract Class getSampleHomeActivity();
   protected abstract int getDescriptionTextId();
   protected abstract int getLinkTextsArrayId();
   protected abstract int getLinkTargetsArrayId();
   protected abstract int getAppTitleResId();
   protected abstract int getCopyrightYearResId();
   protected abstract int getRepositoryLinkResId();
   /**
    * The resource id of the array containing the library titles.
    * If the demo needs no additional library, this method must
    * return -1.
    */
   protected abstract int getLibTitlesArrayId();
   /**
    * The resource id of the array containing the library descriptions.
    * If the demo needs no additional library, this method must
    * return -1.
    */
   protected abstract int getLibDescriptionsArrayId();

   /**
    * Returns the resource id of the usual about text. 
    * If the common text isn't suitable 
    * for a demo the demo, your specific subclass 
    * has to override this method.
    * Make sure that any other id, that you return,
    * is able to deal with four substitution strings.
    */
   protected int getAboutTextId() {
      return R.string.common_about_text;
   }
   
   /**
    * Returns whether the default libs should be included
    * in the about fragment. Unless you override this
    * method to return false the default libs will be shown.
    * 
    * As of now the default libs are Android's support library
    * and the ActionBarCompat library.
    */
   protected boolean getAddDefaultLibs() {
      return true;
   }
   
}
