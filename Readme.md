## Get Started with JavaFX
NOTE: Javafx came with Java 8, but now you need to follow 
> https://openjfx.io/openjfx-docs/#install-java

### Setup JavaFX in IntelliJ
1. Install JavaFX SDK 24 and Java 24.
2. Set SDK to Java 24. Also, keep a check of language level >= 18.
3. Under File > Project Structure > Libraries, add the JavaFX SDK.

### Create JavaFX Project (non-modular way)
1. Right-click on your project/directory where you want your application > New > Module > Select JavaFX.
2. Add Name, location, language, jdk version. Click Next > Create.
3. Comment file module-info.java for non-modular project.
   * https://openjfx.io/openjfx-docs/#next-steps:~:text=For%20a%20non%2Dmodular%20project%2C%20you%20can%20remove%20the%20module%2Dinfo.java%20file.
4. (IMP) Run the Application by providing VM Options containing path to javafx-sdk's /lib folder.
5. In case VM options aren't visible, press alt+V, or under program's run config > Modify Options > Add VM options.
```
--module-path "C:\Users\username\Desktop\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml
```

NOTE: 
1. VM Options (passed to JVM) != Program args (passed to application)
2. Some warnings may come due to Java and JavaFX latest versions, you can suppress them by adding following:
   * --enable-native-access=ALL-UNNAMED
       * https://stackoverflow.com/questions/79725728/how-to-suppress-restricted-method-called-java-lang-systemload-warning-when
   * --sun-misc-unsafe-memory-access=allow
       * https://stackoverflow.com/questions/79525654/errors-after-updating-java-23-and-javafx-22-to-java-24-and-javafx-24
   * --enable-native-access=javafx.graphics
       * No Reference
3. Checkout the openjfx docs to run application using javac / java commands.

### Final VM Options:
```
--module-path "C:\Users\username\Desktop\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow --enable-native-access=javafx.graphics
```

Tip: You can set your installed version of scene builder to view FXML in IntelliJ.
  * https://www.jetbrains.com/help/idea/opening-fxml-files-in-javafx-scene-builder.html#open_files_in_scene_builder_app
