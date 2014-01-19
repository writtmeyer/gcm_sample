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
package com.grokkingandroid.sampleapp.samples.gcm;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class GCMDemoActivity extends BaseActivity {

   private static final String KEY_SPINNER_POS = "keySpinnerPos";

   private int mCurrSpinnerPos = 0;

   @Override
   public void onCreate(Bundle icicle) {
      super.onCreate(icicle);
      setContentView(R.layout.activity_fragment_container);

      if (icicle == null) {
         DemoBaseFragment fragment = GcmDemoFragment.newInstance();
         getSupportFragmentManager().beginTransaction()
               .replace(R.id.demo_fragment_container, fragment).commit();
      }

      final Intent intent = getIntent();
      String msg = intent.getStringExtra(Constants.KEY_MESSAGE_TXT);
      if (msg != null) {
         final NotificationManager manager = 
               (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         manager.cancel(Constants.NOTIFICATION_NR);
         String msgTxt = getString(R.string.msg_received, msg);
         Crouton.showText(this, msgTxt, Style.INFO);            
      }
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      // do not forget to clean up, if necessary
   }

   @Override
   protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      outState.putInt(KEY_SPINNER_POS, mCurrSpinnerPos);
   }

   @Override
   protected Class getSampleHomeActivity() {
      return GCMDemoActivity.class;
   }

   @Override
   protected int getDescriptionTextId() {
      return R.string.gcm_demo_demo_desc;
   }

   @Override
   protected int getLinkTextsArrayId() {
      return R.array.gcm_demo_link_texts;
   }

   @Override
   protected int getLinkTargetsArrayId() {
      return R.array.gcm_demo_link_targets;
   }

   @Override
   protected int getAppTitleResId() {
      return R.string.gcm_demo_app_name;
   }

   @Override
   protected int getCopyrightYearResId() {
      return R.string.gcm_demo_copyright;
   }

   @Override
   protected int getRepositoryLinkResId() {
      return R.string.gcm_demo_repo_link;
   }

   @Override
   protected int getLibTitlesArrayId() {
      return -1;
   }

   @Override
   protected int getLibDescriptionsArrayId() {
      return -1;
   }

}
