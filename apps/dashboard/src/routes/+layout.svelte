<script lang="ts">
	import '../app.css';
	import Navigation from '$lib/components/Navigation.svelte';
	import type { LayoutData } from './$types';
	
	let { children, data }: { children: any; data: LayoutData } = $props();
</script>

<div class="min-h-screen bg-gray-50 dark:bg-gray-900">
	<Navigation session={data.session} />
	<main class="flex-1">
		{@render children()}
	</main>
</div>
