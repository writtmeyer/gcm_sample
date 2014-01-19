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

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grokkingandroid.sampleapp.samples.gcm.R;


public class DescriptionDelegate {
   
   private ViewGroup mLinkContainer;

   @SuppressLint("NewApi")
   void showLinks(ViewGroup container, LayoutInflater inflater, 
         String[] linkTexts, String[] linkTargets) {      
      StringBuilder link = null;
      for (int i = 0; i < linkTexts.length; i++)  {
         final TextView tv = (TextView)inflater.inflate(R.layout.single_blog_link, container, false);//new TextView(getSherlockActivity());
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

   public void addDescriptionAndLinks(LayoutInflater inflater, ViewGroup rootView,
         Resources resources, int descriptionId,
         int linkTextsId, int linkTargetsId) {
      TextView description = (TextView) rootView
            .findViewById(R.id.demoapp_fragment_description);
      Spanned descSpannable= Html.fromHtml(resources.getString(descriptionId));
      description.setText(descSpannable);
      description.setMovementMethod(LinkMovementMethod.getInstance());
      mLinkContainer = 
            (ViewGroup)rootView.findViewById(R.id.container_demo_blog_links);
      String[] linkTexts = resources.getStringArray(linkTextsId);
      String[] linkTargets = resources.getStringArray(linkTargetsId);
      showLinks(mLinkContainer, inflater, linkTexts, linkTargets);
   }

   void clearFocusOnLinks() {
      // since LinkMovementMethod doesn't highlight 
      // the link correctly, I've used a workaround.
      // I set the attribute android:selectAllOnFocus="true"
      // for the TextView in the single_blog_link.xml file.
      // when returnning from the browser, I have to 
      // remove the focus. This is done here:
      if (mLinkContainer != null) {
         int count = mLinkContainer.getChildCount();
         for (int i = 0; i < count; i++) {
            mLinkContainer.getChildAt(i).clearFocus();
         }
      }
   }
}
