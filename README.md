<p align="center">
    <img width="500" src="https://user-images.githubusercontent.com/83756034/199569382-f26d203d-891d-4452-9c4d-9ef859de44f6.png" alt="Material Bread logo">
</p>


<b>1.19 Paper</b> minecraft plugin for selling areas (WorldGuard) using messenger (MySQL) with website.
To install you can follow the instruction below and build it by yourself, or use compiled by github. When you installed it to the plugins folder, you will need to change the <b>config.yml</b> to your database. Currently, you better to have two separete databases - one for 
To edit the plugin in your computer make this: first is to contain everything for plugin, and second for messenging with MySQL and plugin. You could make these in one, but we didn't tested it yet.

You need to install the stable version and build artifacts.
To build them, you need to load your project in  Intellij, then go to File->Project Structure, then go to Artifacts, click + at the top,
Then choose JAR->From modules with dependencies. At the opened tab select the path (I made a path to server's plugin folder) and Include in project build.
