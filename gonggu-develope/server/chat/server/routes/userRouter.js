import express from "express";
const { login } = require("../controllers/login");
const { refresh } = require("../controllers/refresh");
const { authJWT } = require("../controllers/auth");
import { createRoom, getAttachedRooms } from "../controllers/chat";

const userRouter = express.Router();

// api starts with 'users'
userRouter.post("/login", login);
userRouter.post("/refresh", refresh);
userRouter.get("/test", authJWT);
userRouter.post("/:nickname/createRoom", createRoom);
userRouter.get("/:nickname/room", getAttachedRooms);

export default userRouter;
