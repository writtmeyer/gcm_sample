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

import java.io.IOException;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.grokkingandroid.sampleapp.samples.gcm.Constants.EventbusMessageType;
import com.grokkingandroid.sampleapp.samples.gcm.Constants.State;

import de.greenrobot.event.EventBus;

public class GcmIntentService extends IntentService {

   private NotificationManager mNotificationManager;
   private String mSenderId = null;

   public GcmIntentService() {
      super("GcmIntentService");
   }

   @Override
   protected void onHandleIntent(Intent intent) {
      mSenderId = getResources().getString(R.string.gcm_project_id);
      GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

      // action handling for actions of the activity
      String action = intent.getAction();
      Log.v("grokkingandroid", "action: " + action);
      if (action.equals(Constants.ACTION_REGISTER)) {
         register(gcm, intent);
      } else if (action.equals(Constants.ACTION_UNREGISTER)) {
         unregister(gcm, intent);
      } else if (action.equals(Constants.ACTION_ECHO)) {
         sendMessage(gcm, intent);
      }

      // handling of stuff as described on
      // http://developer.android.com/google/gcm/client.html
      try {
         Bundle extras = intent.getExtras();
         // The getMessageType() intent parameter must be the intent you
         // received in your BroadcastReceiver.
         String messageType = gcm.getMessageType(intent);

         if (extras != null && !extras.isEmpty()) { // has effect of
                                                    // unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that
             * GCM will be extended in the future with new message types, just
             * ignore any message types you're not interested in, or that you
             * don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                  .equals(messageType)) {
               sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                  .equals(messageType)) {
               sendNotification("Deleted messages on server: "
                     + extras.toString());
               // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                  .equals(messageType)) {
               // Post notification of received message.
               String msg = extras.getString("message");
               if (TextUtils.isEmpty(msg)) {
                  msg = "empty message";
               }
               sendNotification(msg);
               Log.i("grokkingandroid", "Received: " + extras.toString()
                     + ", sent: " + msg);
            }
         }
      } finally {
         // Release the wake lock provided by the WakefulBroadcastReceiver.
         GcmBroadcastReceiver.completeWakefulIntent(intent);
      }
   }

   private void unregister(GoogleCloudMessaging gcm, Intent intent) {
      try {
         Log.v("grokingandroid", "about to unregister...");
         gcm.unregister();
         Log.v("grokkingandroid", "device unregistered");

         // Persist the regID - no need to register again.
         removeRegistrationId();
         Bundle bundle = new Bundle();
         bundle.putInt(Constants.KEY_EVENT_TYPE,
               EventbusMessageType.UNREGISTRATION_SUCCEEDED.ordinal());
         EventBus.getDefault().post(bundle);
      } catch (IOException e) {
         // If there is an error, don't just keep trying to register.
         // Require the user to click a button again, or perform
         // exponential back-off.

         // I simply notify the user:
         Bundle bundle = new Bundle();
         bundle.putInt(Constants.KEY_EVENT_TYPE,
               EventbusMessageType.UNREGISTRATION_FAILED.ordinal());
         EventBus.getDefault().post(bundle);
         Log.e("grokkingandroid", "Unregistration failed", e);
      }
   }

   private void register(GoogleCloudMessaging gcm, Intent intent) {
      try {
         Log.v("grokingandroid", "about to register...");
         String regid = gcm.register(mSenderId);
         Log.v("grokkingandroid", "device registered: " + regid);

         String account = intent.getStringExtra(Constants.KEY_ACCOUNT);
         sendRegistrationIdToBackend(gcm, regid, account);

         // Persist the regID - no need to register again.
         storeRegistrationId(regid);
         Bundle bundle = new Bundle();
         bundle.putInt(Constants.KEY_EVENT_TYPE,
               EventbusMessageType.REGISTRATION_SUCCEEDED.ordinal());
         bundle.putString(Constants.KEY_REG_ID, regid);
         EventBus.getDefault().post(bundle);
      } catch (IOException e) {
         // If there is an error, don't just keep trying to register.
         // Require the user to click a button again, or perform
         // exponential back-off.

         // I simply notify the user:
         Bundle bundle = new Bundle();
         bundle.putInt(Constants.KEY_EVENT_TYPE,
               EventbusMessageType.REGISTRATION_FAILED.ordinal());
         EventBus.getDefault().post(bundle);
         Log.e("grokkingandroid", "Registration failed", e);
      }
   }

   private void storeRegistrationId(String regId) {
      final SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(this);
      Log.i("grokkingandroid", "Saving regId to prefs: " + regId);
      SharedPreferences.Editor editor = prefs.edit();
      editor.putString(Constants.KEY_REG_ID, regId);
      editor.putInt(Constants.KEY_STATE, State.REGISTERED.ordinal());
      editor.commit();
   }

   private void removeRegistrationId() {
      final SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(this);
      Log.i("grokkingandroid", "Removing regId from prefs");
      SharedPreferences.Editor editor = prefs.edit();
      editor.remove(Constants.KEY_REG_ID);
      editor.putInt(Constants.KEY_STATE, State.UNREGISTERED.ordinal());
      editor.commit();
   }

   private void sendRegistrationIdToBackend(GoogleCloudMessaging gcm,
         String regId, String account) {
      try {
         Bundle data = new Bundle();
         // the name is used for keeping track of user notifications
         // if you use the same name everywhere, the notifications will
         // be cancelled
         data.putString("account", account);
         data.putString("action", Constants.ACTION_REGISTER);
         String msgId = Integer.toString(getNextMsgId());
         gcm.send(mSenderId + "@gcm.googleapis.com", msgId,
               Constants.GCM_DEFAULT_TTL, data);
         Log.v("grokkingandroid", "regId sent: " + regId);
      } catch (IOException e) {
         Log.e("grokkingandroid",
               "IOException while sending registration to backend...", e);
      }
   }

   private void sendMessage(GoogleCloudMessaging gcm, Intent intent) {
      try {
         String msg = intent.getStringExtra(Constants.KEY_MESSAGE_TXT);
         Bundle data = new Bundle();
         data.putString(Constants.ACTION, Constants.ACTION_ECHO);
         data.putString("message", msg);
         String id = Integer.toString(getNextMsgId());
         gcm.send(mSenderId + "@gcm.googleapis.com", id, data);
         Log.v("grokkingandroid", "sent message: " + msg);
      } catch (IOException e) {
         Log.e("grokkingandroid", "Error while sending a message", e);
      }
   }

   // Put the message into a notification and post it.
   // This is just one simple example of what you might choose to do with
   // a GCM message.
   private void sendNotification(String msg) {
      mNotificationManager = (NotificationManager) this
            .getSystemService(Context.NOTIFICATION_SERVICE);

      Intent notificationIntent = new Intent(this, GCMDemoActivity.class);
      notificationIntent.setAction(Constants.NOTIFICATION_ACTION);
      notificationIntent.putExtra(Constants.KEY_MESSAGE_TXT, msg);
      notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            notificationIntent, 0);

      NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_stat_collections_cloud)
            .setContentTitle("GCM Notification")
            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
            .setContentText(msg);

      mBuilder.setContentIntent(contentIntent);
      mNotificationManager.notify(Constants.NOTIFICATION_NR, mBuilder.build());
   }

   private int getNextMsgId() {
      SharedPreferences prefs = getPrefs();
      int id = prefs.getInt(Constants.KEY_MSG_ID, 0);
      Editor editor = prefs.edit();
      editor.putInt(Constants.KEY_MSG_ID, ++id);
      editor.commit();
      return id;
   }

   private SharedPreferences getPrefs() {
      return PreferenceManager.getDefaultSharedPreferences(this);
   }
}
