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

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grokkingandroid.sampleapp.samples.gcm.R;

public abstract class SampleBaseFragment extends Fragment {

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setHasOptionsMenu(true);
   }

   @SuppressLint("NewApi")
   protected void showLinks(ViewGroup container) {      
      String[] linkTexts = getLinkTexts();
      String[] linkTargets = getLinkTargets();
      StringBuilder link = null;
      for (int i = 0; i < linkTexts.length; i++)  {
         // TODO: Use an inflater
         TextView tv = new TextView(getActivity());
         LinearLayout.LayoutParams params = null;
         params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
         if (i != linkTexts.length -1) {
            int marginBottom = getResources().getDimensionPixelSize(R.dimen.linkSpacing);
            params.setMargins(0,  0, 0, marginBottom);
         }
         tv.setLayoutParams(params);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tv.setTextIsSelectable(true);
         }
         tv.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
         link = new StringBuilder(100);
         link.append("<a href=\"");
         link.append(linkTargets[i]);
         link.append("\">");
         link.append(linkTexts[i]);
         link.append("</a>");
         Spanned spannedLink = Html.fromHtml(link.toString());
         tv.setText(spannedLink);
         tv.setMovementMethod(LinkMovementMethod.getInstance());
         container.addView(tv);
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_demo_base,
            container, false);
      if (isFragmentWithInlineDescription()) {
         TextView description = (TextView) rootView
               .findViewById(R.id.demoapp_fragment_description);
         Spanned descSpannable= Html.fromHtml(getResources().getString(getDescriptionTextId()));
         description.setText(descSpannable);
         description.setMovementMethod(LinkMovementMethod.getInstance());
         ViewGroup linkContainer = (ViewGroup)rootView.findViewById(R.id.container_demo_blog_links);
         showLinks(linkContainer);
         linkContainer.invalidate();
      }
      else {
         rootView.findViewById(R.id.container_demo_description).setVisibility(View.GONE);
         rootView.findViewById(R.id.container_demo_blog_links).setVisibility(View.GONE);
      }
      
      // delegate to subclass for content view
      ViewGroup contentContainer = 
            (ViewGroup)rootView.findViewById(R.id.container_demo_content);
      onCreateContentView(inflater, contentContainer, savedInstanceState);
      return rootView;
   }

   public abstract String[] getLinkTexts();
   
   public abstract String[] getLinkTargets();
   
   public void onCreateContentView(LayoutInflater inflater, 
         ViewGroup container, Bundle savedInstanceState) {
      //TODO: temporary solution;
      // either generate it or do it in a better way
      // works for now, though
      container.setVisibility(View.GONE);
   }

   /**
    * Fragments without any visibile content should return true. In this
    * case the description will be presented inline. Otherwise
    * the description will be displayed after the user has selected
    * the corresponding menu item in the action bar.
    * <br><br>
    * By default this method returns false.
    */
   public boolean isFragmentWithInlineDescription() {
      return false;
   }

   public abstract int getDescriptionTextId();
   
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      if (isFragmentWithInlineDescription()) {
         menu.findItem(R.id.menu_description).setVisible(false);
      }
      addFragmentSpecificMenu(menu, inflater);
   }

   protected abstract void addFragmentSpecificMenu(Menu menu, MenuInflater inflater);
   
}