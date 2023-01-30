import { axios } from "@bundled-es-modules/axios";

export default function () {
  const { VITE_BASE_URL } = import.meta.env;

  const axiosGet = (
    URL,
    JWT = null,
    params = null,
    onSuccess = null,
    onFailed = null
  ) => {
    const final_URL = URL.startsWith("http") ? URL : VITE_BASE_URL + URL;
    console.log("axiosGet", final_URL, JWT);
    axios
      .get(final_URL, {
        params,
        headers: {
          "X-AUTH-TOKEN": JWT,
        },
      })
      .then((resp) => {
        if (resp.status === 200 || resp.data.ok === true) {
          if (onSuccess) onSuccess(resp.data);
          else if (onFailed) onFailed(resp.data);
        }
      })
      .catch((err) => {
        if (onFailed) onFailed(err);
      });
  };

  const axiosPost = (
    URL,
    JWT = null,
    Data,
    onSuccess = null,
    onFailed = null
  ) => {
    const final_URL = URL.startsWith("http") ? URL : VITE_BASE_URL + URL;
    console.log("axiosPost", final_URL, Data);
    axios
      .post(final_URL, JSON.stringify(Data), {
        headers: {
          "Content-Type": `application/json`,
          "X-AUTH-TOKEN": JWT,
        },
      })
      .then((resp) => {
        if (resp.status === 200 || resp.data.ok === true) {
          if (onSuccess) onSuccess(resp.data);
        } else {
          if (onFailed) onFailed(resp.data);
        }
      })
      .catch((err) => {
        onFailed(err.response.data.message); //for loginprompt
      });
  };

  const axiosPatch = (
    URL,
    JWT = null,
    Data,
    onSuccess = null,
    onFailed = null
  ) => {
    const final_URL = URL.startsWith("http") ? URL : VITE_BASE_URL + URL;
    console.log("axiosPatch", final_URL, Data);
    axios
      .patch(final_URL, JSON.stringify(Data), {
        headers: {
          "Content-Type": `application/json`,
          "X-AUTH-TOKEN": JWT,
        },
      })
      .then((resp) => {
        if (resp.status === 200 || resp.data.ok === true) {
          if (onSuccess) onSuccess(resp.data);
        } else {
          if (onFailed) onFailed(resp.data);
        }
      })
      .catch((err) => {
        onFailed(err.response.data.message); //for loginprompt
      });
  };

  const axiosDelete = (URL, JWT = null, onSuccess = null, onFailed = null) => {
    const final_URL = URL.startsWith("http") ? URL : VITE_BASE_URL + URL;
    console.log("axiosDelete", final_URL);
    axios
      .delete(final_URL, {
        headers: {
          "X-AUTH-TOKEN": JWT,
        },
      })
      .then((resp) => {
        if (resp.status === 200 || resp.data.ok === true) {
          if (onSuccess) onSuccess(resp.data);
          else if (onFailed) onFailed(resp.data);
        }
      });
  };
  return { axiosGet, axiosPost, axiosPatch, axiosDelete };
}
