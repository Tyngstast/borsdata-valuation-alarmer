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
  apns: {
    headers: {
      "apns-priority": "5",
    },
    payload: {
      aps: {
        contentAvailable: true,
      },
    },
  },
};

exports.triggerWorkers = functions
  .region("europe-west1")
  .pubsub
  .schedule("0 09-18 * * 1-5")
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

const devMessage = {
  topic: "devTriggerValuationAlarmWorker",
  data: {
    trigger: "true",
  },
  apns: {
    headers: {
      "apns-priority": "5",
    },
    payload: {
      aps: {
        contentAvailable: true,
      },
    },
  },
};

exports.devTriggerWorkers = functions
  .region("europe-west1")
  .pubsub
  .schedule("0 09-18 * * 1-5")
  .timeZone("Europe/Stockholm")
  .onRun((context) => {
    functions.logger.info("DEV schedule... Executing", context);
    getMessaging()
      .send(devMessage)
      .then((response) => {
        functions.logger.info("DEV Successfully sent message", response);
      })
      .catch((error) => {
        functions.logger.error("DEV Error sending message", error);
      });

    return null;
  });
