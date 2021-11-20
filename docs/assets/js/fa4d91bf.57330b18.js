"use strict";(self.webpackChunkdoodle_docs=self.webpackChunkdoodle_docs||[]).push([[930],{8215:function(n,e,o){var t=o(7294);e.Z=function(n){var e=n.children,o=n.hidden,a=n.className;return t.createElement("div",{role:"tabpanel",hidden:o,className:a},e)}},6396:function(n,e,o){o.d(e,{Z:function(){return m}});var t=o(7462),a=o(7294),l=o(2389),i=o(9443);var r=function(){var n=(0,a.useContext)(i.Z);if(null==n)throw new Error('"useUserPreferencesContext" is used outside of "Layout" component.');return n},s=o(9521),d=o(6010),p="tabItem_vU9c";function u(n){var e,o,t,l=n.lazy,i=n.block,u=n.defaultValue,m=n.values,c=n.groupId,v=n.className,g=a.Children.map(n.children,(function(n){if((0,a.isValidElement)(n)&&void 0!==n.props.value)return n;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof n.type?n.type:n.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})),k=null!=m?m:g.map((function(n){var e=n.props;return{value:e.value,label:e.label}})),b=(0,s.lx)(k,(function(n,e){return n.value===e.value}));if(b.length>0)throw new Error('Docusaurus error: Duplicate values "'+b.map((function(n){return n.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.');var h=null===u?u:null!=(e=null!=u?u:null==(o=g.find((function(n){return n.props.default})))?void 0:o.props.value)?e:null==(t=g[0])?void 0:t.props.value;if(null!==h&&!k.some((function(n){return n.value===h})))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+h+'" but none of its children has the corresponding value. Available values are: '+k.map((function(n){return n.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");var f=r(),j=f.tabGroupChoices,y=f.setTabGroupChoices,w=(0,a.useState)(h),N=w[0],T=w[1],$=[],_=(0,s.o5)().blockElementScrollPositionUntilNextRender;if(null!=c){var C=j[c];null!=C&&C!==N&&k.some((function(n){return n.value===C}))&&T(C)}var V=function(n){var e=n.currentTarget,o=$.indexOf(e),t=k[o].value;t!==N&&(_(e),T(t),null!=c&&y(c,t))},x=function(n){var e,o=null;switch(n.key){case"ArrowRight":var t=$.indexOf(n.currentTarget)+1;o=$[t]||$[0];break;case"ArrowLeft":var a=$.indexOf(n.currentTarget)-1;o=$[a]||$[$.length-1]}null==(e=o)||e.focus()};return a.createElement("div",{className:"tabs-container"},a.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,d.Z)("tabs",{"tabs--block":i},v)},k.map((function(n){var e=n.value,o=n.label;return a.createElement("li",{role:"tab",tabIndex:N===e?0:-1,"aria-selected":N===e,className:(0,d.Z)("tabs__item",p,{"tabs__item--active":N===e}),key:e,ref:function(n){return $.push(n)},onKeyDown:x,onFocus:V,onClick:V},null!=o?o:e)}))),l?(0,a.cloneElement)(g.filter((function(n){return n.props.value===N}))[0],{className:"margin-vert--md"}):a.createElement("div",{className:"margin-vert--md"},g.map((function(n,e){return(0,a.cloneElement)(n,{key:e,hidden:n.props.value!==N})}))))}function m(n){var e=(0,l.Z)();return a.createElement(u,(0,t.Z)({key:String(e)},n))}},694:function(n,e,o){o.r(e),o.d(e,{frontMatter:function(){return d},contentTitle:function(){return p},metadata:function(){return u},toc:function(){return m},default:function(){return v}});var t=o(7462),a=o(3366),l=(o(7294),o(3905)),i=o(6396),r=o(8215),s=["components"],d={hide_title:!0},p="Installation",u={unversionedId:"installation",id:"installation",isDocsHomePage:!1,title:"Installation",description:"Doodle apps are built using Gradle, like other Kotlin JS or Multi-Platform projects.",source:"@site/docs/installation.mdx",sourceDirName:".",slug:"/installation",permalink:"/doodle/docs/installation",tags:[],version:"current",frontMatter:{hide_title:!0},sidebar:"tutorialSidebar",previous:{title:"Hello Doodle",permalink:"/doodle/docs/introduction"},next:{title:"Applications",permalink:"/doodle/docs/applications"}},m=[{value:"Pure JS Project",id:"pure-js-project",children:[],level:2},{value:"Pure JVM Project",id:"pure-jvm-project",children:[],level:2},{value:"Multi-platform Project",id:"multi-platform-project",children:[],level:2}],c={toc:m};function v(n){var e=n.components,o=(0,a.Z)(n,s);return(0,l.kt)("wrapper",(0,t.Z)({},c,o,{components:e,mdxType:"MDXLayout"}),(0,l.kt)("h1",{id:"installation"},"Installation"),(0,l.kt)("p",null,"Doodle apps are built using ",(0,l.kt)("a",{parentName:"p",href:"http://www.gradle.org"},"Gradle"),", like other Kotlin JS or Multi-Platform projects.\nLearn more by checking out  the Kotlin ",(0,l.kt)("a",{parentName:"p",href:"https://kotlinlang.org/docs/getting-started.html"},"docs"),"."),(0,l.kt)("h2",{id:"pure-js-project"},"Pure JS Project"),(0,l.kt)("p",null,"You can set up a pure Javascript app with the following build scripts."),(0,l.kt)(i.Z,{groupId:"language",mdxType:"Tabs"},(0,l.kt)(r.Z,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin",metastring:"title=build.gradle.kts",title:"build.gradle.kts"},'plugins {\n    id ("org.jetbrains.kotlin.js") version "1.5.30"\n}\n\nversion = "1.0.0"\ngroup   = "com.my.cool.app"\n\nrepositories {\n    mavenCentral()\n}\n\nkotlin {\n    js().browser()\n\n    val doodleVersion = "0.6.0" // <--- Latest Doodle version\n\n    dependencies {\n        implementation ("io.nacular.doodle:core:$doodleVersion"   )\n        implementation ("io.nacular.doodle:browser:$doodleVersion")\n\n        // Optional\n        // implementation ("io.nacular.doodle:controls:$doodleVersion" )\n        // implementation ("io.nacular.doodle:animation:$doodleVersion")\n        // implementation ("io.nacular.doodle:themes:$doodleVersion"   )\n    }\n}\n'))),(0,l.kt)(r.Z,{value:"groovy",label:"Groovy",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-groovy",metastring:"title=build.gradle",title:"build.gradle"},"plugins {\n    id 'org.jetbrains.kotlin.js' version '1.5.30'\n}\n\nversion = '1.0.0'\ngroup   = 'com.my.cool.app'\n\nrepositories {\n    mavenCentral()\n}\n\next {\n    doodle_version = '0.6.0' // <--- Latest Doodle version\n}\n\nkotlin {\n    js().browser()\n\n    dependencies {\n        implementation \"io.nacular.doodle:core:$doodle_version\"\n        implementation \"io.nacular.doodle:browser:$doodle_version\"\n\n        // Optional\n        // implementation \"io.nacular.doodle:controls:$doodle_version\"\n        // implementation \"io.nacular.doodle:animation:$doodle_version\"\n        // implementation \"io.nacular.doodle:themes:$doodle_version\"\n    }\n}\n")))),(0,l.kt)("h2",{id:"pure-jvm-project"},"Pure JVM Project"),(0,l.kt)("p",null,"You can set up a pure JVM app with the following build scripts."),(0,l.kt)(i.Z,{groupId:"language",mdxType:"Tabs"},(0,l.kt)(r.Z,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin",metastring:"title=build.gradle.kts",title:"build.gradle.kts"},'plugins {\n    id ("org.jetbrains.kotlin.jvm") version "1.5.30"\n    application\n}\n\nversion = "1.0.0"\ngroup   = "com.my.cool.app"\n\nrepositories {\n    mavenCentral()\n    maven {\n        url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev")\n    }\n}\n\nkotlin {\n    target.compilations.all {\n        kotlinOptions {\n            jvmTarget = "11"\n        }\n    }\n\n    val doodleVersion = "0.6.0" // <--- Latest Doodle version\n\n    dependencies {\n        implementation ("io.nacular.doodle:core:$doodleVersion"   )\n        implementation ("io.nacular.doodle:desktop:$doodleVersion")\n\n        // Optional\n        // implementation ("io.nacular.doodle:controls:$doodleVersion" )\n        // implementation ("io.nacular.doodle:animation:$doodleVersion")\n        // implementation ("io.nacular.doodle:themes:$doodleVersion"   )\n    }\n}\n\napplication {\n    mainClass.set("YOUR_CLASS")\n}\n'))),(0,l.kt)(r.Z,{value:"groovy",label:"Groovy",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-groovy",metastring:"title=build.gradle",title:"build.gradle"},'plugins {\n    id \'org.jetbrains.kotlin.jvm\' version \'1.5.30\'\n    id \'application\'\n}\n\nversion = \'1.0.0\'\ngroup   = \'com.my.cool.app\'\n\nrepositories {\n    mavenCentral()\n    maven {\n        url "https://maven.pkg.jetbrains.space/public/p/compose/dev"\n    }\n}\n\next {\n    doodle_version = \'0.6.0\' // <--- Latest Doodle version\n}\n\nkotlin {\n    target.compilations.all {\n        kotlinOptions {\n            jvmTarget = "11"\n        }\n    }\n\n    dependencies {\n        implementation "io.nacular.doodle:core:$doodle_version"\n        implementation "io.nacular.doodle:desktop:$doodle_version"\n\n        // Optional\n        // implementation "io.nacular.doodle:controls:$doodle_version"\n        // implementation "io.nacular.doodle:animation:$doodle_version"\n        // implementation "io.nacular.doodle:themes:$doodle_version"\n    }\n}\n\napplication {\n    mainClassName = "YOUR_CLASS"\n}\n')))),(0,l.kt)("h2",{id:"multi-platform-project"},"Multi-platform Project"),(0,l.kt)("p",null,"Doodle is a set of Kotlin Multi-platform (MPP) libraries. Which means you can create an MPP for your app as well. The advantage of this\nis that you can write your app entirely (except for ",(0,l.kt)("inlineCode",{parentName:"p"},"main"),") in ",(0,l.kt)("inlineCode",{parentName:"p"},"common")," code and make it available on both Web (JS) and Desktop (JVM). The\nfollowing shows how to create such an app."),(0,l.kt)("div",{className:"admonition admonition-tip alert alert--success"},(0,l.kt)("div",{parentName:"div",className:"admonition-heading"},(0,l.kt)("h5",{parentName:"div"},(0,l.kt)("span",{parentName:"h5",className:"admonition-icon"},(0,l.kt)("svg",{parentName:"span",xmlns:"http://www.w3.org/2000/svg",width:"12",height:"16",viewBox:"0 0 12 16"},(0,l.kt)("path",{parentName:"svg",fillRule:"evenodd",d:"M6.5 0C3.48 0 1 2.19 1 5c0 .92.55 2.25 1 3 1.34 2.25 1.78 2.78 2 4v1h5v-1c.22-1.22.66-1.75 2-4 .45-.75 1-2.08 1-3 0-2.81-2.48-5-5.5-5zm3.64 7.48c-.25.44-.47.8-.67 1.11-.86 1.41-1.25 2.06-1.45 3.23-.02.05-.02.11-.02.17H5c0-.06 0-.13-.02-.17-.2-1.17-.59-1.83-1.45-3.23-.2-.31-.42-.67-.67-1.11C2.44 6.78 2 5.65 2 5c0-2.2 2.02-4 4.5-4 1.22 0 2.36.42 3.22 1.19C10.55 2.94 11 3.94 11 5c0 .66-.44 1.78-.86 2.48zM4 14h5c-.23 1.14-1.3 2-2.5 2s-2.27-.86-2.5-2z"}))),"tip")),(0,l.kt)("div",{parentName:"div",className:"admonition-content"},(0,l.kt)("p",{parentName:"div"},"App ",(0,l.kt)("a",{parentName:"p",href:"/doodle/docs/applications#app-launch"},(0,l.kt)("strong",{parentName:"a"},"launch code"))," is the only portion that needs to be in ",(0,l.kt)("inlineCode",{parentName:"p"},"js")," or ",(0,l.kt)("inlineCode",{parentName:"p"},"jvm"),"."))),(0,l.kt)(i.Z,{groupId:"language",mdxType:"Tabs"},(0,l.kt)(r.Z,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin",metastring:"title=build.gradle.kts",title:"build.gradle.kts"},'plugins {\n    id ("org.jetbrains.kotlin.multiplatform") version "1.5.30"\n    application\n}\n\nversion = "1.0.0"\ngroup   = "com.my.cool.app"\n\nrepositories {\n    mavenCentral()\n    maven {\n        url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev")\n    }\n}\n\nkotlin {\n    js().browser()\n\n    jvm {\n        withJava()\n        compilations.all {\n            kotlinOptions {\n                jvmTarget = "11"\n            }\n        }\n    }\n\n    val doodleVersion = "0.6.0" // <--- Latest Doodle version\n\n    sourceSets {\n        val commonMain by getting {\n            dependencies {\n                implementation ("io.nacular.doodle:core:$doodleVersion")\n\n                // Optional\n                // implementation ("io.nacular.doodle:controls:$doodleVersion" )\n                // implementation ("io.nacular.doodle:animation:$doodleVersion")\n                // implementation ("io.nacular.doodle:themes:$doodleVersion"   )\n            }\n        }\n\n        val jsMain by getting {\n            dependencies {\n                implementation ("io.nacular.doodle:browser:$doodleVersion")\n            }\n        }\n\n        val jvmMain by getting {\n            dependencies {\n                implementation ("io.nacular.doodle:desktop:$doodleVersion")\n            }\n        }\n    }\n}\n\napplication {\n    mainClass.set("YOUR_CLASS")\n}\n'))),(0,l.kt)(r.Z,{value:"groovy",label:"Groovy",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-groovy",metastring:"title=build.gradle",title:"build.gradle"},'plugins {\n    id \'org.jetbrains.kotlin.multiplatform\' version \'1.5.30\'\n    id \'application\'\n}\n\nversion = \'1.0.0\'\ngroup   = \'com.my.cool.app\'\n\nrepositories {\n    mavenCentral()\n    maven {\n        url "https://maven.pkg.jetbrains.space/public/p/compose/dev"\n    }\n}\n\next {\n    doodle_version = \'0.6.0\' // <--- Latest Doodle version\n}\n\nkotlin {\n    js().browser()\n\n    jvm {\n        withJava()\n        compilations.all {\n            kotlinOptions {\n                jvmTarget = "11"\n            }\n        }\n    }\n\n    sourceSets {\n        commonMain.dependencies {\n            implementation "io.nacular.doodle:core:$doodle_version"\n\n            // Optional\n            // implementation "io.nacular.doodle:controls:$doodle_version"\n            // implementation "io.nacular.doodle:animation:$doodle_version"\n            // implementation "io.nacular.doodle:themes:$doodle_version"\n        }\n\n        jsMain.dependencies {\n            implementation "io.nacular.doodle:browser:$doodle_version"\n        }\n\n        jvmMain.dependencies {\n            implementation "io.nacular.doodle:desktop:$doodle_version"\n        }\n    }\n}\n\napplication {\n    mainClassName = "YOUR_CLASS"\n}\n')))))}v.isMDXComponent=!0}}]);