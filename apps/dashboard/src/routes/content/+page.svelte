<script lang="ts">
	import {
		Card,
		CardContent,
		CardDescription,
		CardHeader,
		CardTitle,
	} from "$lib/components/ui/card";
	import { Badge } from "$lib/components/ui/badge";
	import { getRelativeDate } from "@/date-utils";
	import { truncateText } from "@/text-utils";
	import type { PageProps } from "./$houdini";

	let { data }: PageProps = $props();
	let { ContentQuery } = $derived(data);

	let pageIndex = $state(0);
	let contentItems = $derived($ContentQuery?.data?.submissions || []);

	function getStatusColor(status: string) {
		switch (status) {
			case "processed":
				return "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300";
			case "pending":
				return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300";
			case "archived":
				return "bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300";
			default:
				return "bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300";
		}
	}

	async function nextPage() {
		pageIndex++;
		await ContentQuery.fetch({ variables: { pageIndex, pageSize: 10 } });
	}

	async function previousPage() {
		if (pageIndex > 0) {
			pageIndex--;
		}

		await ContentQuery.fetch({ variables: { pageIndex, pageSize: 10 } });
	}
</script>

<svelte:head>
	<title>Content Items - Agenttalks Dashboard</title>
</svelte:head>

<div class="container mx-auto p-6 pt-8">
	<div class="mb-8">
		<h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">
			Content Items
		</h1>
		<p class="text-gray-600 dark:text-gray-400">
			Manage and review content items for podcast episodes
		</p>
	</div>

	<!-- Content Items List -->
	<Card>
		<CardHeader>
			<CardTitle>Content Items</CardTitle>
			<CardDescription>
				Manage and review content items for podcast episodes
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
									{item!.title || item!.url}
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
								{truncateText(item!.summary || "")}
							</p>

							<!-- Metadata row -->
							<div
								class="flex items-center justify-between text-xs text-muted-foreground"
							>
								<div class="flex items-center space-x-3">
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
								<div class="flex items-center space-x-2">
									<Badge
										class={getStatusColor(
											item!.status || "submitted",
										)}
									>
										{item!.status || "submitted"}
									</Badge>
								</div>
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
					<p class="text-sm">No content items found.</p>
				</div>
			{/if}
			<!-- Pagination Controls -->
			<div class="mt-6 pt-4 border-t">
				<div class="flex items-center justify-between">
					<div class="flex items-center space-x-2">
						{#if pageIndex > 0 }
						<button
							onclick={previousPage}
							class="px-3 py-1 text-xs border rounded-md hover:bg-muted/50 disabled:opacity-50 disabled:cursor-not-allowed"
						>
							Previous
						</button>
						{/if}
						{#if contentItems.length > 0}
						<button
							onclick={nextPage}
							class="px-3 py-1 text-xs border rounded-md hover:bg-muted/50 disabled:opacity-50 disabled:cursor-not-allowed"
						>
							Next
						</button>
						{/if}
					</div>
				</div>
			</div>
		</CardContent>
	</Card>
</div>
