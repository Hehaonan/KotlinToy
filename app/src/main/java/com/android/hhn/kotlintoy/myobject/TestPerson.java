package com.android.hhn.kotlintoy.myobject;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * @author hehaonan @ Zhihu Inc.
 * @since 2022-03-08
 */
public class TestPerson {
    public static void main(String[] args) {

        TestObject myTest = new TestObject();
        myTest.setOnClickListener(new TestObject.OnClickListener() {

            @Override
            public void onClick(@NonNull View v) {

            }
        });
        myTest.setMethod(new M() {
            @Override
            public void funM() {

            }
        });

        Person.Singleton.work();
        Log.d("TAG", "main: " + Person.Singleton.FLAG);

        Person2.Singleton.work();
        Log.d("TAG", "main: " + Person2.FLAG);
    }
}
