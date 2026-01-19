import {Country} from "./Country";
import {Likes} from "./Likes";

export type Photo = {
    id: string;
    user: User;
    src: string;
    country: Country;
    description: string;
    likes: Likes;
}

type User = {
    id: string,
}