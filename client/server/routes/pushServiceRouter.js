const router = require("express").Router();
const db = require("../tools/db");
const webpush = require("web-push");

webpush.setVapidDetails(
  "mailto:2022capstone16.5@gmail.com",
  process.env.VAPID_PUBLIC,
  process.env.VAPID_PRIVATE
);

router.post("/register/:id", async (req, res) => {
  try {
    const userId = req.params.id;
    const body = { ...req.body };

    await db.query(
      `INSERT INTO device(nickname, endpoint, auth, p256dh) VALUES ('${userId}', '${body.endpoint}', '${body.keys.auth}', '${body.keys.p256dh}')`
    );

    res.status(200).send("Ok");
  } catch (error) {
    console.log(error);
    res.status(500).json({
      message: "something went wrong",
    });
  }
});

router.post("/unregister", async (req, res) => {
  try {
    const { endpoint } = req.body;
    const [result] = await db.query(
      `DELETE FROM device WHERE endpoint = "${endpoint}"`
    );
    console.log(result);
    res.status(200).send("Ok");
  } catch (error) {
    console.log(error);
    res.status(500).send("Something sent wrong");
  }
});

router.post("/send", async (req, res) => {
  try {
    const { sendTo } = req.body;
    const [result] = await db.query(
      `SELECT * FROM device WHERE nickname="${sendTo}"`
    );
    result.forEach((device) => {
      let pushConfig = {
        endpoint: device.endpoint,
        keys: {
          auth: device.auth,
          p256dh: device.p256dh,
        },
      };

      webpush
        .sendNotification(
          pushConfig,
          JSON.stringify({
            pTitle: "공구 희망자가 생겼습니다.",
            pMsg: "공구 희망자가 생겼습니다. 내 공구 목록에서 확인해보세요.",
          })
        )
        .catch((err) => {
          console.log(err);
        });
    });
    res.send("Ok");
  } catch (error) {
    console.log(error);
    res.send("Something went wrong");
  }
});

module.exports = router;
