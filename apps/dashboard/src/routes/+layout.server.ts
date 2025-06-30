import type { LayoutServerLoad } from './$types';
import { redirect } from '@sveltejs/kit';

export const load: LayoutServerLoad = async (event) => {
    const session = await event.locals.auth();

    if (!session) {
        throw redirect(302, '/auth/signin/keycloak');
    }

    return {
        session
    };
};
