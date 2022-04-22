import * as functions from "firebase-functions";
import { initializeApp } from "firebase-admin/app";
import { getMessaging } from "firebase-admin/messaging";

initializeApp();

const topic = "triggerValuationAlarmWorker";
const message = {
  data: {
    trigger: "true",
  },
  topic,
};

exports.triggerWorkers = functions
  .region("europe-west1")
  .pubsub
  .schedule("*/30 09-23 * * 1-5")
  .timeZone("Europe/Stockholm")
  .onRun((context) => {
    functions.logger.info("Triggered schedule... Executing", context);
    getMessaging()
      .send(message)
      .then((response) => {
        functions.logger.info("Successfully sent message", response);
      })
      .catch((error) => {
        functions.logger.error("Error sending message", error);
      });

    return null;
  });
