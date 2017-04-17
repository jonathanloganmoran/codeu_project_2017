// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.io.*;

public final class TestRunner {

  private static boolean loading = true;
  private static int FRAME_SPEED = 60;

  /**
   * updated to a different thread as not to interfere
   * with the loading message animation
   *
   * @param msg: the message to be displayed in the loading
   */
  public static synchronized void main(String[] args) {
    Thread th = new Thread() {
      @Override
      public void run() {
        try {
          loading("Testing In Progress ... ");
          final Result result =
              JUnitCore.runClasses(
                  codeu.chat.common.SecretTest.class,
                  codeu.chat.common.UuidTest.class,
                  codeu.chat.common.UuidsTest.class,
                  codeu.chat.relay.ServerTest.class,
                  codeu.chat.server.BasicControllerTest.class,
                  codeu.chat.server.DatabaseTest.class,
                  codeu.chat.server.RawControllerTest.class,
                  codeu.chat.util.store.StoreTest.class
              );
          loading = false;
          Thread.sleep(FRAME_SPEED*2);
          int numFailures = 0;
          for (final Failure failure : result.getFailures()) {
            numFailures++;
          }
          // Print the results of the test.
          String results = "\rResult:                     ";
          if(result.wasSuccessful()){
            results += "\n\tPassed\n";
          } else {
            results += "\n\tFailed " + numFailures + " test(s):\n";
          }
          System.out.write(results.getBytes());
          for (final Failure failure : result.getFailures()) {
            System.out.println("\t\t"+failure.toString());
          }
          System.out.println();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    th.start();
  }

  /**
   * Give the user a loading screen while tests are running
   * created due to the unpredictability of how long it will
   * take for the SQL server to respond.
   *
   * @param msg: the message to be displayed in the loading
   */
  private static synchronized void loading(String msg) {
    Thread th = new Thread() {
      @Override
      public void run() {
        try {
          System.out.write("\r|".getBytes());
          int x =0;
          while(loading) {
            String anim= "|/-\\";
            String data = "\r" + msg + anim.charAt(++x % anim.length());
            System.out.write(data.getBytes());
            Thread.sleep(FRAME_SPEED);
          }
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };
    th.start();
  }
}
