import express from "express";
import {
  getAllRooms,
  enterRoom,
  checkUserInRoom,
  getAttachedRooms,
  getChat,
  postChat,
  postSendNotification,
  updateEnterTime,
  dummyController,
} from "../controllers/chat";

const chatRouter = express.Router();

// api starts with rooms
chatRouter.get("/", getAllRooms);
chatRouter.get("/:nickname", getAttachedRooms);
chatRouter.get("/:roomId/nickname/:nickname", checkUserInRoom);
chatRouter.get("/:roomId/chat", getChat);
chatRouter.post("/chat", postChat);
chatRouter.post("/notification", postSendNotification);
chatRouter.post("/time", updateEnterTime);
chatRouter.get("/dummy", dummyController);
//chatRouter.post("/enter", enterRoom);

export default chatRouter;
