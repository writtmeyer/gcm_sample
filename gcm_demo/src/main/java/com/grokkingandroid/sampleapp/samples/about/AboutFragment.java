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
package com.grokkingandroid.sampleapp.samples.about;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grokkingandroid.sampleapp.samples.gcm.R;


public class AboutFragment extends DialogFragment {

   private static final String KEY_RESOURCE_IDS = "keyResIds";
   private static final String KEY_ADD_DEFAULT_LIBS = "keyAddDefaultLibs";

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      Resources res = getResources();
      if (getArguments() == null) {
         throw new IllegalStateException("Arguments bundle must not be empty");
      }
      int[] resIds = getArguments().getIntArray(KEY_RESOURCE_IDS);
      getDialog().setTitle(res.getString(R.string.about));
      View view = inflater.inflate(R.layout.fragment_about, container, false);
      ViewGroup libParent = (ViewGroup)view.findViewById(R.id.about_container);

      String[] libTitles = null;
      String[] libDescriptions = null;
      if (resIds[0] != -1) {
         libTitles = res.getStringArray(resIds[0]);
         libDescriptions = res.getStringArray(resIds[1]);
      }
      if (getArguments().getBoolean(KEY_ADD_DEFAULT_LIBS, true)) {
         if (resIds[0] == -1) {
            libTitles = res.getStringArray(R.array.grokkingandroidsample_about_titles);
            libDescriptions = res.getStringArray(R.array.grokkingandroidsample_about_contents);
         }
         else {
            String[] defaultTitles = res.getStringArray(R.array.grokkingandroidsample_about_titles);
            String[] target = new String[defaultTitles.length + libTitles.length];
            System.arraycopy(libTitles, 0, target, 0, libTitles.length);
            System.arraycopy(defaultTitles, 0, target, libTitles.length, defaultTitles.length);
            libTitles = target;
            String[] defaultDescriptions = res.getStringArray(R.array.grokkingandroidsample_about_contents);
            target = new String[defaultDescriptions.length + libTitles.length];
            System.arraycopy(libDescriptions, 0, target, 0, libDescriptions.length);
            System.arraycopy(defaultDescriptions, 0, target, libDescriptions.length, defaultDescriptions.length);
            libDescriptions = target;
         }
      }
      String libraryPlural = res.getQuantityString(R.plurals.plural_libraries, libTitles.length);
      String appTitle = res.getString(resIds[3]);
      String copyrightYear = res.getString(resIds[4]);
      String repositoryLink = res.getString(resIds[5]);
      String aboutText = res.getString(resIds[2], appTitle, copyrightYear, repositoryLink, libraryPlural);
      Spanned spannedAboutText = Html.fromHtml(aboutText);
      TextView aboutTv = (TextView)libParent.findViewById(R.id.about_text);
      aboutTv.setText(spannedAboutText);
      aboutTv.setMovementMethod(LinkMovementMethod.getInstance());

      if (libTitles != null) {
         for (int i = 0; i < libTitles.length; i++) {
            View libContainer = inflater.inflate(R.layout.single_library_layout, libParent, false);
            TextView currLibTitle = (TextView)libContainer.findViewById(R.id.library_title);
            currLibTitle.setText(libTitles[i]);
            TextView currLibDesc = (TextView)libContainer.findViewById(R.id.library_text);
            Spanned spanned = Html.fromHtml(libDescriptions[i]);
            currLibDesc.setText(spanned);
            currLibDesc.setMovementMethod(LinkMovementMethod.getInstance());
            libParent.addView(libContainer);
         }
      }
      return view;
   }

   public static AboutFragment newInstance(int[] resourceIds, boolean useDefaultLibs) {
      AboutFragment f = new AboutFragment();
      Bundle bundle = new Bundle();
      bundle.putIntArray(KEY_RESOURCE_IDS, resourceIds);
      bundle.putBoolean(KEY_ADD_DEFAULT_LIBS, useDefaultLibs);
      f.setArguments(bundle);
      return f;
   }

}
