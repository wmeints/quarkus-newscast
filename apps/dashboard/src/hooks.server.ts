import { sequence } from '@sveltejs/kit/hooks';
import type { Handle } from '@sveltejs/kit';
import { handle as handleAuth } from './auth';

const attachAuthSession: Handle = async ({ event, resolve }) => {
    const session = await event.locals.auth();

    if (session?.accessToken) {
        event.locals.accessToken = session.accessToken;
    }

    return resolve(event);
};

export const handle = sequence(
    handleAuth,
    attachAuthSession
);
