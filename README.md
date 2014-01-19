#Readme
This is a sample project to showcase Google Cloud Messaging using upstream messaging.
The project was originally created for a talk I held at the [Dutch Android User Group](www.dutchaug.org) 
meeting in Utrecht on January, 16th 2014. If you ever have the possibility to 
visit a meeting of the Dutch AUG, I stringly recommend to do so. It has been a gorgeous evening!


##Libraries used
This demo makes of [EventBus](https://github.com/greenrobot/EventBus), [Google Play Services](https://developer.android.com/google/play-services/index.html), the Support Library, and ActionBarCompat. Thus you have to add those to your project:

    dependencies {
        compile 'com.google.android.gms:play-services:4.0.30'
        compile 'com.android.support:appcompat-v7:+'
        compile 'com.android.support:support-v4:19.0.0'
        compile 'de.greenrobot:eventbus:2.2.0'
        compile 'de.keyboardsurfer.android.widget:crouton:1.8.2'
    }


##Most important classes
All important classes are within the `com.grokkingandroid.sampleapp.samples.gcm` package.

The initial starting acvtivity is `GcmDemoActivity.java`. Most logic though is contained
in `GcmDemoFragments`.

The BroadcastRecevier `GcmBroadcastReceiver` just passes the intent through to the 
IntentService `GcmIntentService` which handles calls from Google's cloud. The
`GcmIntentService` is also used by the `GcmDemoFragment` for all upstream calls.


Not all sources of the demo might have appropriate license annotation. Nevertheless: The license for the code
used in this demo is the Apache Software License. You can find the license in the `LICENSE-2.0.txt` file at the root of the project. 


##Credentials
**To run this project you need a GCM-project number.** You can read more about it on the 
[Getting Started page of Google's documentation](http://developer.android.com/google/gcm/gs.html).

I suggest to put the project number into a separate resource file named `gcm_strings.xml`. This file is already excluded 
from the repository by the current `.gitignore` file.

Keep in mind: For using the newer features of Google Cloud Messaging (upstream messaging / user notifications) 
**you need to apply first**. See for example the note at the top of the [http://developer.android.com/google/gcm/notifications.html](User Notification page). 
You can find a current link there as well.

##Relevant Blogposts on [Grokking Android](http://www.grokkingandroid.com/)
Right now I haven't finished the blog post about GCM. It's in the making and should be up pretty soon.


##Warning: Generated codebase

**Please note:** I have created the core of this sample using a custom generator. 
I'm going to publish many samples when writing blog posts and want to keep my work 
for those as focused as possible. The generator helps me achieve this even if it 
means that some code might seem a bit odd. I always refer to the relevant classes 
at the beginning of the README file.

**tl;dr:** Do not copy without thinking - should go without saying, but it's better to mention it once more!


##Developed by

*Wolfram Rittmeyer* - You can contact me via:

* [Grokking Android (my blog)](http://www.grokkingandroid.com)

* [Google+](https://plus.google.com/+WolframRittmeyer)

* [Twitter](https://twitter.com/RittmeyerW)


##Thanks
A big thanks to the organizers of the meetup in Utrecht. It was a great evening with good presentations 
and interesting conversations in the breaks!

Also thanks to all my readers and blog commenters. Your feedback helps me to stay on track and to go on 
with projects like this. Without my blog (and thus you) I probably woouln't have been invited in the first place :-)

Special thanks also to the Android crowd on G+. You're an awesome crowd. I have gained many insights 
by reading posts there, by following links to blog posts or by discussions on G+! And it was great to meet some of you
in Utrecht. 

And finally: This readme was created using [dillinger.io](http://dillinger.io). Thanks for this service.


##License
    Copyright 2014 Wolfram Rittmeyer

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

