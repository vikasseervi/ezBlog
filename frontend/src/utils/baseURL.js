const getBaseURL = () => {
    const currentHost = window?.location?.host;
    const currentProtocol = window?.location?.protocol || "http:";
    const baseURL = `${currentProtocol}//${currentHost.replace("3000", "8080")}`;
    return baseURL;
};



export const baseURL = getBaseURL();