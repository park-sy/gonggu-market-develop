const db = require("../tools/db");
// db is a pool
import io from "../server";

export const createRoom = async (req, res) => {
  const { roomName } = req.body;
  const { nickname } = req.params;
  console.log("✅ Got RoomName: ", roomName);
  console.log("✅ Got NickName: ", nickname);

  if (roomName === undefined || nickname === undefined) {
    return res.status(401).json({
      ok: false,
    });
  }
  try {
    const result = await db.query(
      `INSERT INTO Room(nickname, roomName) VALUES('${nickname}','${roomName}');`
    );
    //console.log(result);
    const newId = result[0].insertId;
    await db.query(
      `INSERT INTO Member(roomId,nickname) VALUES('${newId}','${nickname}');`
    );
    return res.status(200).json({
      ok: true,
    });
  } catch (err) {
    console.log(err);
    return res.status(401).json({
      ok: false,
    });
  }
};

export const getAllRooms = async (req, res) => {
  try {
    const rooms = await db.query(
      `SELECT deal_id, title from deal GROUP BY deal_id;`
    );
    return res.status(200).json({ ok: true, rooms: rooms[0] });
  } catch (err) {
    console.log("❌ Failed to fetch Rooms from DB:", err);
    return res.status(401).json({});
  }
};

export const getAttachedRooms = async (req, res) => {
  const { nickname } = req.params;
  if (nickname === undefined) {
    return res.status(401).json({ rooms: [] });
  }
  try {
    const myRooms = await db.query(
      `WITH TBL AS (SELECT DISTINCT deal.deal_id, entered_at, deal.title FROM deal_member JOIN deal WHERE deal_member.nickname = '${nickname}' and deal_member.deal_id = deal.deal_id and deal.deletion = 0)
      SELECT TBL.deal_id, title, COUNT(chat_id) as notRead FROM TBL LEFT JOIN chat ON TBL.entered_at < chat.created_at and chat.deal_id = TBL.deal_id and chat.chat_type <> 'notification' GROUP BY deal_id;`
    );
    return res.status(200).json({ ok: true, rooms: myRooms[0] });
  } catch (err) {
    return res.status(401).json({ ok: false, rooms: [] });
  }
};

export const enterRoom = async (req, res) => {
  //console.log(req.body);
  const { roomId, nickname } = req.body;
  console.log("roomId", roomId);
  console.log("nickname", nickname);
  if (roomId === undefined || nickname === undefined) {
    console.log("❌ Undefined");
    return res.status(401).json({ ok: false, errorMessage: "Failed..." });
  }
  try {
    const [result] = await db.query(
      `SELECT COUNT(*) as CNT FROM Member WHERE roomId = ${roomId} and nickname = '${nickname}';`
    );
    const memCnt = result[0]["CNT"];
    //console.log("memCnt:", memCnt);
    if (memCnt) {
      return res.status(200).json({ ok: false });
    }
    await db.query(
      `INSERT INTO Member(roomId,nickname) VALUES('${roomId}','${nickname}');`
    );
    return res.status(200).json({ ok: true });
  } catch (err) {
    console.log("❌ Error", err);
    return res.status(401).json({ ok: false });
  }
};

export const checkUserInRoom = async (req, res) => {
  const { roomId, nickname } = req.params;
  //console.log(roomId, nickname);
  try {
    const [result] = await db.query(
      `SELECT COUNT(*) as CNT FROM deal_member WHERE deal_id = ${roomId} and nickname = '${nickname}';`
    );
    const memCnt = result[0]["CNT"];
    //console.log("memcnt:", memCnt);
    if (memCnt > 0) {
      return res.status(200).json({
        ok: true,
      });
    } else {
      return res.status(200).json({
        ok: false,
      });
    }
  } catch (err) {
    return res.status(200).json({
      ok: false,
    });
  }
};

export const getChat = async (req, res) => {
  console.log("GET CHAT:", req.params);
  const { roomId } = req.params;
  if (roomId === undefined) {
    return res.status(401).json({ ok: false });
  }
  if (String(roomId) === "0")
    return res.status(200).json({ ok: true, msgList: [] });
  try {
    await db.query(
      `UPDATE deal_member SET entered_at=now() WHERE deal_member.deal_id = '${roomId}';`
    );
    const [result] = await db.query(
      `SELECT nickname, content, chat_type, created_at, deal_id, image_path FROM chat WHERE deal_id = '${roomId}';`
    );
    //console.log(result);
    return res.status(200).json({ ok: true, msgList: result });
  } catch (err) {
    console.log(err);
    return res.status(401).json({ ok: false, msgList: [] });
  }
};

export const postChat = async (req, res) => {
  //console.log(req.body);
  const { content, sender, imgPath, roomId, chatType } = req.body;
  const replacedContent = String(content).replace(/'/g, "''");
  try {
    await db.query(
      `INSERT INTO chat(deal_id,nickname,chat_type,content,image_path) VALUES('${roomId}','${sender}','${chatType}','${replacedContent}','${imgPath}');`
    );
    return res.status(200).json({ ok: true });
  } catch (err) {
    console.log(err);
    return res.status(401).json({ ok: false });
  }
};

export const postSendNotification = async (req, res) => {
  try {
    const { roomId, nickname, inOut } = req.body;
    const msgObj = new Object();
    msgObj.chatType = "notification";
    msgObj.time = new Date(Date.now());
    msgObj.sender = nickname;
    msgObj.roomId = String(roomId);
    msgObj.imgPath = "";
    if (inOut === true) msgObj.content = `${nickname}님이 들어왔습니다.`;
    else msgObj.content = `${nickname}님이 나갔습니다.`;
    io.to(String(roomId)).emit("notify", { msg: msgObj });

    await db.query(
      `INSERT INTO Chat(roomId,nickname,chatType,content,imagePath) VALUES('${roomId}','${nickname}','${msgObj.chatType}','${msgObj.content}','${msgObj.imgPath}');`
    );

    return res.status(200).json({ ok: true, msg: msgObj });
  } catch (err) {
    console.log(err);
    return res.status(400).json({ ok: false });
  }
};

export const updateEnterTime = async (req, res) => {
  console.log("✅ Update Time", req.body.roomId);
  const { roomId } = req.body;
  if (roomId === undefined) {
    return res.status(401).json({ ok: false });
  }
  if (String(roomId) === "0") return res.status(200).json({ ok: true });
  try {
    await db.query(
      `UPDATE deal_member SET entered_at=now() WHERE deal_member.deal_id = '${roomId}';`
    );
    return res.status(200).json({ ok: true });
  } catch (err) {
    console.log(err);
    return res.status(401).json({ ok: false });
  }
};

export const dummyController = async (req, res) => {
  return res.status(200).json({ ok: true });
};
