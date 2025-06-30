import { SvelteKitAuth, type DefaultSession } from '@auth/sveltekit';
import Keycloak from '@auth/sveltekit/providers/keycloak';
import { KEYCLOAK_ISSUER, KEYCLOAK_CLIENT_ID, KEYCLOAK_CLIENT_SECRET } from '$env/static/private';

declare module '@auth/core/types' {
    interface Session extends DefaultSession {
        accessToken?: string;
    }
}

export const { handle, signIn, signOut } = SvelteKitAuth({
    providers: [Keycloak({
        issuer: KEYCLOAK_ISSUER,
        clientId: KEYCLOAK_CLIENT_ID,
        clientSecret: KEYCLOAK_CLIENT_SECRET,
    })],
    callbacks: {
        async jwt({ token, account }) {
            if (account) {
                token.accessToken = account.access_token;
            }

            return token;
        },
        async session({ session, token }) {
            session.accessToken = token.accessToken as string;
            return session;
        }
    }
});