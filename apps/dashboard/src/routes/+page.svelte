<script lang="ts">
	import {
		Card,
		CardContent,
		CardDescription,
		CardHeader,
		CardTitle,
	} from "$lib/components/ui/card";
	import { Badge } from "$lib/components/ui/badge";
	import { getWeekStart, formatDate, getRelativeDate } from "$lib/date-utils";
	import { truncateText } from "$lib/text-utils";
	import type { PageProps } from "./$houdini";

	let { data }: PageProps = $props();
	let { dashboard } = $derived(data);

	let statistics = $dashboard.data?.statistics;
	let contentItems = $dashboard.data?.recentSubmissions || [];
	let lastUpdated = new Date();

	let weekStart = getWeekStart();
</script>

<svelte:head>
	<title>Home - Agenttalks Dashboard</title>
</svelte:head>

<div class="container mx-auto p-6 pt-8">
	<div class="mb-8">
		<h1 class="text-4xl font-bold tracking-tight mb-2">Dashboard</h1>
		<p class="text-muted-foreground text-lg">
			Welcome to the agenttalks content management dashboard
		</p>
	</div>

	<!-- Mobile Submit Content Button -->
	<div class="md:hidden mb-6">
		<a
			href="/content/submit"
			class="flex items-center justify-center w-full bg-blue-600 hover:bg-blue-700 text-white px-6 py-4 rounded-lg text-lg font-semibold transition-colors duration-200 shadow-lg hover:shadow-xl"
		>
			<svg
				class="w-6 h-6 mr-2"
				fill="none"
				stroke="currentColor"
				viewBox="0 0 24 24"
			>
				<path
					stroke-linecap="round"
					stroke-linejoin="round"
					stroke-width="2"
					d="M12 4v16m8-8H4"
				></path>
			</svg>
			Submit Content
		</a>
	</div>

	<!-- Metrics Grid -->
	<div class="grid gap-6 md:grid-cols-2 lg:grid-cols-2">
		<!-- Content Items This Week Card -->
		<Card class="relative overflow-hidden">
			<CardHeader
				class="flex flex-row items-center justify-between space-y-0 pb-2"
			>
				<CardTitle class="text-sm font-medium"
					>Content Items Submitted</CardTitle
				>
				<svg
					xmlns="http://www.w3.org/2000/svg"
					viewBox="0 0 24 24"
					fill="none"
					stroke="currentColor"
					stroke-linecap="round"
					stroke-linejoin="round"
					stroke-width="2"
					class="h-4 w-4 text-muted-foreground"
				>
					<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
					<circle cx="9" cy="7" r="4"></circle>
					<path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
					<path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
				</svg>
			</CardHeader>
			<CardContent>
				<div class="text-2xl font-bold">
					{statistics?.totalEpisodes}
				</div>
				<p class="text-xs text-muted-foreground">
					Submissions collected since {formatDate(weekStart)}
				</p>
				<div class="mt-4">
					<Badge variant="secondary" class="text-xs">This Week</Badge>
				</div>
			</CardContent>
		</Card>

		<!-- Podcast Episodes Created Card -->
		<Card class="relative overflow-hidden">
			<CardHeader
				class="flex flex-row items-center justify-between space-y-0 pb-2"
			>
				<CardTitle class="text-sm font-medium"
					>Podcast Episodes Created</CardTitle
				>
				<svg
					xmlns="http://www.w3.org/2000/svg"
					viewBox="0 0 24 24"
					fill="none"
					stroke="currentColor"
					stroke-linecap="round"
					stroke-linejoin="round"
					stroke-width="2"
					class="h-4 w-4 text-muted-foreground"
				>
					<polygon points="11 5,6 9,2 9,2 15,6 15,11 19,11 5"
					></polygon>
					<path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path>
					<path d="M19.07 4.93a10 10 0 0 1 0 14.14"></path>
				</svg>
			</CardHeader>
			<CardContent>
				<div class="text-2xl font-bold">
					{statistics?.totalEpisodes}
				</div>
				<p class="text-xs text-muted-foreground">
					Total episodes produced
				</p>
				<div class="mt-4">
					<Badge variant="outline" class="text-xs">All Time</Badge>
				</div>
			</CardContent>
		</Card>
	</div>

	<!-- Content Items List -->
	<div class="mt-8">
		<Card>
			<CardHeader>
				<CardTitle>This Week's Content Items</CardTitle>
				<CardDescription>
					Collected content ready for your next podcast episode
				</CardDescription>
			</CardHeader>
			<CardContent>
				<div class="space-y-4">
					{#each contentItems as item (item!.id)}
						<div
							class="p-4 border rounded-lg hover:bg-muted/50 transition-colors"
						>
							<div class="space-y-3">
								<!-- Title and URL -->
								<div class="flex items-start justify-between">
									<h4
										class="font-medium text-sm leading-relaxed flex-1 pr-4"
									>
										{item!.title}
									</h4>
									<a
										href={item!.url}
										target="_blank"
										rel="noopener noreferrer"
										class="text-primary hover:underline text-xs flex items-center space-x-1 flex-shrink-0"
									>
										<span>View</span>
										<svg
											class="w-3 h-3"
											fill="none"
											stroke="currentColor"
											viewBox="0 0 24 24"
										>
											<path
												stroke-linecap="round"
												stroke-linejoin="round"
												stroke-width="2"
												d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"
											></path>
										</svg>
									</a>
								</div>

								<!-- Summary -->
								<p
									class="text-sm text-muted-foreground leading-relaxed"
								>
									{truncateText(item!.summary || '')}
								</p>

								<!-- Date -->
								<div
									class="flex items-center text-xs text-muted-foreground"
								>
									<span class="flex items-center space-x-1">
										<svg
											class="w-3 h-3"
											fill="none"
											stroke="currentColor"
											viewBox="0 0 24 24"
										>
											<path
												stroke-linecap="round"
												stroke-linejoin="round"
												stroke-width="2"
												d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
											></path>
										</svg>
										<span
											>{getRelativeDate(
												item!.dateCreated || new Date(),
											)}</span
										>
									</span>
								</div>
							</div>
						</div>
					{/each}
				</div>

				{#if contentItems.length === 0}
					<div class="text-center py-8 text-muted-foreground">
						<svg
							class="w-12 h-12 mx-auto mb-4 opacity-50"
							fill="none"
							stroke="currentColor"
							viewBox="0 0 24 24"
						>
							<path
								stroke-linecap="round"
								stroke-linejoin="round"
								stroke-width="2"
								d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
							></path>
						</svg>
						<p class="text-sm">
							No content items collected this week
						</p>
						<p class="text-xs mt-1">
							Start collecting content to see it here
						</p>
					</div>
				{/if}

				<div class="mt-6 pt-4 border-t">
					<div class="flex items-center justify-between">
						<p class="text-xs text-muted-foreground">
							Last updated: {lastUpdated.toLocaleTimeString()}
						</p>
						<a href="/content" class="text-xs text-primary hover:underline">
							View all content →
						</a>
					</div>
				</div>
			</CardContent>
		</Card>
	</div>
</div>
