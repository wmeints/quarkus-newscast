import type { Actions } from './$types';
import { fail, redirect } from '@sveltejs/kit';
import { SubmitContentStore } from '$houdini';

export const actions: Actions = {
    submit: async (event) => {
        // Check if user is authenticated
        const session = await event.locals.auth();

        if (!session) {
            throw redirect(302, '/auth/signin/keycloak');
        }

        const data = await event.request.formData();
        const url = data.get('url')?.toString();

        if (!url) {
            return fail(400, { message: 'URL is required' });
        }

        // Basic URL validation
        try {
            const urlObj = new URL(url);
            if (urlObj.protocol !== 'http:' && urlObj.protocol !== 'https:') {
                return fail(400, { message: 'URL must use http:// or https://' });
            }
        } catch {
            return fail(400, { message: 'Invalid URL format' });
        }

        const submitContent = new SubmitContentStore();
        await submitContent.mutate({ url }, { event });
    }
};
