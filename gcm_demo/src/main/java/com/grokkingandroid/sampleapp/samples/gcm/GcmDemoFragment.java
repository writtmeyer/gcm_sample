/*
 * Copyright (C) 2013 Wolfram Rittmeyer
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

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.grokkingandroid.sampleapp.samples.gcm.Constants.EventbusMessageType;
import com.grokkingandroid.sampleapp.samples.gcm.Constants.State;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class GcmDemoFragment extends DemoBaseFragment implements
      View.OnClickListener {

   private static final int RC_RES_REQUEST = 100;
   private static final int RC_SELECT_ACCOUNT = 200;
   private Button mBtnRegister;
   private Button mBtnMessage;
   private TextView mTxtAccountName;
   private TextView mTxtRegId;
   private TextView mTxtMsg;
   private State mState = State.UNREGISTERED;

   public static GcmDemoFragment newInstance() {
      return new GcmDemoFragment();
   }

   @Override
   public void onCreateContentView(LayoutInflater inflater,
         ViewGroup container, Bundle savedInstanceState) {
      Log.v("grokking", "onCreateContentView " + container);
      if (!checkPlayServices()) {
         inflater.inflate(
               R.layout.container_content_no_play_services, container, true);
         return;
      }
      // get current state from prefs
      mState = getCurrState();

      View root = inflater.inflate(R.layout.container_content_gcm_demo, container, true);
      mBtnRegister = (Button) root.findViewById(R.id.btn_register);
      mBtnRegister.setOnClickListener(this);

      mTxtRegId = (TextView)root.findViewById(R.id.txt_reg_id);
      mBtnMessage = (Button) root.findViewById(R.id.btn_send_message);
      if (mState != State.REGISTERED && mState != State.UNREGISTERED) {
         mBtnMessage.setEnabled(false);
      }
      else {
         if (mState == State.REGISTERED) {
            mBtnRegister.setText(R.string.btn_unregister);
            mTxtRegId.setText(getRegId());
         }
         mBtnMessage.setOnClickListener(this);
      }

      mTxtMsg = (TextView)root.findViewById(R.id.txt_message);
      mTxtAccountName = (TextView)root.findViewById(R.id.txt_user_account);
      Button btnSelectAccount = (Button)root.findViewById(R.id.btn_select_account);
      btnSelectAccount.setOnClickListener(this);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
         btnSelectAccount.setVisibility(View.GONE);
      }
      Log.v("grokking", "onCreateContentView");
   }

   private State getCurrState() {
      SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(getActivity());
      int stateAsInt = prefs.getInt(Constants.KEY_STATE,
            State.UNREGISTERED.ordinal());
      return State.values()[stateAsInt];
   }

   private String getRegId() {
      SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(getActivity());
      return prefs.getString(Constants.KEY_REG_ID, null);
   }

   @Override
   public void onPause() {
      super.onPause();
      EventBus.getDefault().unregister(this);
   }

   @Override
   public void onResume() {
      super.onResume();
      EventBus.getDefault().register(this);
   }

   @Override
   public String[] getLinkTexts() {
      return getResources().getStringArray(R.array.gcm_demo_link_texts);
   }

   @Override
   public String[] getLinkTargets() {
      return getResources().getStringArray(R.array.gcm_demo_link_targets);
   }

   @Override
   public int getDescriptionTextId() {
      return R.string.gcm_demo_demo_desc;
   }

   @Override
   protected void addFragmentSpecificMenu(Menu menu, MenuInflater inflater) {
   }

   @Override
   public void onClick(View view) {
      Log.v("grokkingandroid", "onClick: " + view.getId());
      if (view.getId() == R.id.btn_register) {
         mBtnRegister.setEnabled(false);
         mBtnMessage.setEnabled(false);
         switch (mState) {
         case REGISTERED:
            unregisterDevice();
            break;
         case UNREGISTERED:
            registerDevice();
            break;
         default:
            Log.e("grokkingandroid", "click event on register button while it should be deactiviated");
            break;
         }
      } else if (view.getId() == R.id.btn_send_message) {
         sendMessage();
      } else if (view.getId() == R.id.btn_select_account) {
         startAccountSelector();
      }
   }

   @TargetApi(value=Build.VERSION_CODES.ICE_CREAM_SANDWICH)
   private void startAccountSelector() {
      Intent selectAccount = 
            AccountManager.
                  newChooseAccountIntent(
                        null, 
                        null, 
                        new String[]{"com.google"}, 
                        false, 
                        null,
                        null,
                        null,
                        null);
      startActivityForResult(selectAccount, RC_SELECT_ACCOUNT);
   }

   private void registerDevice() {
      Intent regIntent = new Intent(getActivity(), GcmIntentService.class);
      if (!TextUtils.isEmpty(mTxtAccountName.getText())) {
         regIntent.putExtra(Constants.KEY_ACCOUNT, mTxtAccountName.getText().toString());
      }
      else {
         regIntent.putExtra(Constants.KEY_ACCOUNT, Constants.DEFAULT_USER);
      }
      regIntent.setAction(Constants.ACTION_REGISTER);
      getActivity().startService(regIntent);
   }

   private void unregisterDevice() {
      Intent regIntent = new Intent(getActivity(), GcmIntentService.class);
      regIntent.setAction(Constants.ACTION_UNREGISTER);
      getActivity().startService(regIntent);
   }

   private void sendMessage() {
      Intent msgIntent = new Intent(getActivity(), GcmIntentService.class);
      msgIntent.setAction(Constants.ACTION_ECHO);
      String msg;
      if (!TextUtils.isEmpty(mTxtMsg.getText())) {
         msg = mTxtMsg.getText().toString();
         mTxtMsg.setText("");
      }
      else {
         msg = getActivity().getString(R.string.no_message);
      }
      String msgTxt = getString(R.string.msg_sent, msg);
      Crouton.showText(getActivity(), msgTxt, Style.INFO);            
      msgIntent.putExtra(Constants.KEY_MESSAGE_TXT, msg);
      getActivity().startService(msgIntent);
   }

    /**
     * EventBus messages.
     */
   public void onEventMainThread(Bundle bundle) {
      int typeOrdinal = bundle.getInt(Constants.KEY_EVENT_TYPE);
      EventbusMessageType type = EventbusMessageType.values()[typeOrdinal];
      switch (type) {
      case REGISTRATION_FAILED:
         mBtnRegister.setEnabled(true);
         break;
      case REGISTRATION_SUCCEEDED:
         mBtnRegister.setText(R.string.btn_unregister);
         mBtnRegister.setEnabled(true);
         mBtnMessage.setEnabled(true);
         mTxtRegId.setText(bundle.getString(Constants.KEY_REG_ID));
         mState = State.REGISTERED;
         break;
      case UNREGISTRATION_FAILED:
         mBtnRegister.setEnabled(true);
         mBtnMessage.setEnabled(true);
         break;
      case UNREGISTRATION_SUCCEEDED:
         mBtnRegister.setText(R.string.btn_register);
         mBtnRegister.setEnabled(true);
         mTxtRegId.setText("");
         mState = State.UNREGISTERED;
         break;
      }    
   }
   
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == RC_SELECT_ACCOUNT) {
         if (resultCode == Activity.RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            mTxtAccountName.setText(accountName);
         }
         else {
            Log.v("grokkingandroid", "couldn't select account: " + resultCode);
         }
      }
   }

   // taken more or less verbatim from the documentation:
   //
   /**
    * Check the device to make sure it has the Google Play Services APK. If it
    * doesn't, display a dialog that allows users to download the APK from the
    * Google Play Store or enable it in the device's system settings.
    */
   private boolean checkPlayServices() {
      int resultCode = GooglePlayServicesUtil
            .isGooglePlayServicesAvailable(getActivity());
      if (resultCode != ConnectionResult.SUCCESS) {
         if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                  RC_RES_REQUEST).show();
         } else {
            Log.i("grokkingandroid", "This device is not supported.");
         }
         return false;
      }
      return true;
   }
}
