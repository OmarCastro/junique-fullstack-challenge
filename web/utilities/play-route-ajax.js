
export const routes = window.playRoutes;

export function request({method, url, data, contentType} = {}) {
    const fetchCommand = method === "GET" ?
        () => fetch(url) :
        () => {
            const body = data == null ? "" : typeof data === "string" ? data : JSON.stringify(data);
            const headers = {};
            if(contentType != null && typeof contentType === "string"){
                headers["Content-type"] = contentType;
            } else if(data != null && typeof data !== "string"){
                headers["Content-type"] = 'application/json';
            }
            return fetch(url, { method, body, headers })
        };
    return fetchCommand()
        .then(function(response) {
            if (!response.ok) {
                throw Error(response.statusText);
            }
            return response;
        })
}