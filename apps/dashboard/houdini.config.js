/// <references types="houdini-svelte">

export default {
    watchSchema: {
        url: 'http://localhost:8080/graphql/',
    },
    runtimeDir: '.houdini',
    plugins: {
        'houdini-svelte': {
            forceRunesMode: true,
        },
    },
    scalars: {
        BigInteger: {
            type: 'number',
            marshal(value) {
                return value;
            },
            unmarshal(value) {
                return value ? +value : 0;
            }
        },
        DateTime: {
            type: 'Date',
            marshal(value) {
                return value instanceof Date ? value.toISOString() : new Date(value).toISOString();
            },
            unmarshal(value) {
                return value ? new Date(value) : null;
            }
        }
    }
}

// export default config
