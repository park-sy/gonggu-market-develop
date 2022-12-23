import dayjs from "dayjs";

const endDate = "2022-12-14T15:00:00Z";
let ends = dayjs(endDate);
ends = ends.add(1, "day");

let today = dayjs();
console.log("today", today.format("YYYY-MM-DD HH:mm:ss"));
console.log("dateDiff", ends.isBefore(today));
console.log("EndDate", ends.format("YYYY-MM-DD HH:mm:ss"));

function parseDay(target) {
  let endDate = dayjs(target + "Z");
  return endDate.add(1, "day");
}

/**
 * @param {string} ends 비교하는 날짜
 * @returns {number} 현재부터 ends 까지 남은 '일'
 * */
function daysLeft(ends) {
  const today = dayjs();
  const endDate = parseDay(ends);
  return endDate.diff(today, "d");
}

/**
 * @param {string} date 비교하는 날짜
 * @returns {boolean} 오늘 이전의 시점인가
 * */
function isPassed(date) {
  const today = dayjs();
  const targetDate = parseDay(date);
  return targetDate.isBefore(today);
}

/**
 * @param {string} date 바꾸려는 날짜
 * @returns {string} YYYY-MM-DD
 * */
function parseToYMD(date) {
  let target = dayjs(date);
  return target.format("YYYY-MM-DD");
}

function parseToYMDHmS(date) {
  let target = dayjs(date + "Z");
  return target.format("YYYY-MM-DD HH:mm:ss");
}

export { parseDay, daysLeft, isPassed, parseToYMD, parseToYMDHmS };
