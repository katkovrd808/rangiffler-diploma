import {ApolloClient, createHttpLink, InMemoryCache} from "@apollo/client";
import {setContext} from "@apollo/client/link/context";
import {idTokenFromLocalStorage} from "./authUtils";


const API_URL = `${import.meta.env.VITE_API_URL}`;

const apolloHttpLink = createHttpLink({
    uri: `${API_URL}/graphql`,
})

const headerLink = setContext((_request, previousContext) => ({
    headers: {
        ...previousContext.headers,
        "Authorization": idTokenFromLocalStorage() ? `Bearer ${idTokenFromLocalStorage()}` : "",
    },
}));

export const apiClient = new ApolloClient({
    link: headerLink.concat(apolloHttpLink),
    cache: new InMemoryCache(),
});