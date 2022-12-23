export default function urlBase64ToUint8Array(string) {
  const padding = "=".repeat((4 - (string.length % 4)) % 4);
  const base64 = (string + padding)
    /*eslint-disable*/
    .replace(/\-/g, "+")
    /*eslint-enable*/
    .replace(/_/g, "/");
  const rawdata = window.atob(base64);
  return Uint8Array.from([...rawdata].map((char) => char.charCodeAt(0)));
}
