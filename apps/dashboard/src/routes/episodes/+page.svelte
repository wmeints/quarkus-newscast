<script lang="ts">
	import {
		Card,
		CardContent,
		CardDescription,
		CardHeader,
		CardTitle,
	} from "$lib/components/ui/card";
	import type { PageProps } from "./$houdini";
	import { formatDate } from "@/date-utils";

	let { data }: PageProps = $props();
	let { episodes } = $derived(data);
	let items = $episodes.data?.episodes || [];
</script>

<svelte:head>
	<title>Podcast Episodes - Agenttalks Dashboard</title>
</svelte:head>

<div class="container mx-auto p-6 pt-8">
	<div class="mb-8">
		<h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">
			Podcast Episodes
		</h1>
		<p class="text-gray-600 dark:text-gray-400">
			Manage your podcast episodes and track performance
		</p>
	</div>

	<!-- Episodes Grid -->
	<div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
		{#each items as episode}
			<Card class="hover:shadow-lg transition-shadow duration-200">
				<CardHeader>
					<CardTitle class="text-lg">{episode!.title}</CardTitle>
					<CardDescription>{episode!.description}</CardDescription>
				</CardHeader>
				<CardContent>
					<div class="space-y-3">
						<div
							class="flex justify-between text-sm text-gray-600 dark:text-gray-400"
						>
							<span>Publish Date:</span>
							<span>{formatDate(episode!.dateCreated || new Date())}</span>
						</div>
					</div>
				</CardContent>
			</Card>
		{/each}
	</div>

	{#if items.length === 0}
		<Card>
			<CardContent>
				<div class="text-center py-16 text-muted-foreground">
					<svg
						class="w-16 h-16 mx-auto mb-4 opacity-50"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
					>
						<polygon points="11 5,6 9,2 9,2 15,6 15,11 19,11 5"></polygon>
						<path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path>
						<path d="M19.07 4.93a10 10 0 0 1 0 14.14"></path>
					</svg>
					<h3 class="text-lg font-medium mb-2">No episodes yet</h3>
					<p class="text-sm">
						Hmm, no episodes yet...
					</p>
					<p class="text-xs mt-1">
						Start collecting content and we'll create an episode next Friday!
					</p>
				</div>
			</CardContent>
		</Card>
	{/if}
</div>
