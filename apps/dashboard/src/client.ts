import { HoudiniClient } from '$houdini';
import { PUBLIC_CONTENT_API_URL } from '$env/static/public';

export default new HoudiniClient({
    url: PUBLIC_CONTENT_API_URL,
    fetchParams({ session }) {
        return {
            headers: {
                Authorization: session?.accessToken ? `Bearer ${session?.accessToken}` : '',
            }
        }
    }
})
