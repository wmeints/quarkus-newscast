<script lang="ts">
	import { enhance } from "$app/forms";
	import { Badge } from "$lib/components/ui/badge";
	import {
		ExternalLink,
		Send,
		AlertCircle,
		CheckCircle,
	} from "lucide-svelte";

	let url = $state("");
	let description = $state("");
	let submitting = $state(false);
	let formMessage = $state("");
	let formError = $state("");

	function isValidUrl(urlString: string): boolean {
		try {
			const urlObj = new URL(urlString);
			return urlObj.protocol === "http:" || urlObj.protocol === "https:";
		} catch (e) {
			return false;
		}
	}

	function handleSubmit() {
		formError = "";
		formMessage = "";

		if (!url.trim()) {
			formError = "URL is required";
			return;
		}

		if (!isValidUrl(url)) {
			formError =
				"Please enter a valid URL (must start with http:// or https://)";
			return;
		}

		submitting = true;
	}

	function resetForm() {
		url = "";
		description = "";
		formMessage = "";
		formError = "";
	}
</script>

<svelte:head>
	<title>Submit Content - Agenttalks Dashboard</title>
</svelte:head>

<div class="container mx-auto px-4 py-8 max-w-2xl">
	<div class="mb-8">
		<h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-4">
			Submit Content
		</h1>
		<p class="text-gray-600 dark:text-gray-400">
			Share interesting content by providing a URL. Our system will
			automatically scrape and process the content.
		</p>
	</div>

	<div
		class="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-lg shadow-sm p-6"
	>
		<form
			method="POST"
			action="?/submit"
			use:enhance={() => {
				handleSubmit();
				return async ({ result, update }) => {
					submitting = false;
					if (result.type === "success") {
						formMessage =
							"Content submitted successfully! We'll process it shortly.";
						resetForm();
					} else if (result.type === "failure") {
						formError =
							(result.data as any)?.message ||
							"Failed to submit content. Please try again.";
					}
					await update();
				};
			}}
		>
			<div class="space-y-6">
				<!-- URL Input -->
				<div class="space-y-2">
					<label
						for="url"
						class="block text-sm font-medium text-gray-700 dark:text-gray-300"
					>
						Content URL *
					</label>
					<div class="relative">
						<input
							id="url"
							name="url"
							type="url"
							bind:value={url}
							placeholder="https://example.com/article"
							required
							disabled={submitting}
							class="w-full pl-10 pr-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white disabled:opacity-50 disabled:cursor-not-allowed"
						/>
						<ExternalLink
							size={16}
							class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
						/>
					</div>
					<p class="text-xs text-gray-500 dark:text-gray-400">
						Enter the URL of the article, blog post, or other
						content you'd like to submit.
					</p>
				</div>

				<!-- Description Input -->
				<div class="space-y-2">
					<label
						for="description"
						class="block text-sm font-medium text-gray-700 dark:text-gray-300"
					>
						Description (Optional)
					</label>
					<textarea
						id="description"
						name="description"
						bind:value={description}
						placeholder="Add a brief description or your thoughts about this content..."
						rows="3"
						disabled={submitting}
						class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white disabled:opacity-50 disabled:cursor-not-allowed resize-y"
					></textarea>
					<p class="text-xs text-gray-500 dark:text-gray-400">
						Optional: Add context or explain why this content is
						interesting.
					</p>
				</div>

				<!-- Error Message -->
				{#if formError}
					<div
						class="flex items-center space-x-2 p-3 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-md"
					>
						<AlertCircle
							size={16}
							class="text-red-600 dark:text-red-400 flex-shrink-0"
						/>
						<p class="text-sm text-red-700 dark:text-red-300">
							{formError}
						</p>
					</div>
				{/if}

				<!-- Success Message -->
				{#if formMessage}
					<div
						class="flex items-center space-x-2 p-3 bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-md"
					>
						<CheckCircle
							size={16}
							class="text-green-600 dark:text-green-400 flex-shrink-0"
						/>
						<p class="text-sm text-green-700 dark:text-green-300">
							{formMessage}
						</p>
					</div>
				{/if}

				<!-- Submit Button -->
				<button
					type="submit"
					disabled={submitting || !url.trim() || !isValidUrl(url)}
					class="w-full flex items-center justify-center space-x-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-200"
				>
					{#if submitting}
						<div class="flex items-center space-x-2">
							<div
								class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
							></div>
							<span>Submitting...</span>
						</div>
					{:else}
						<Send size={16} />
						<span>Submit Content</span>
					{/if}
				</button>
			</div>
		</form>
	</div>

	<!-- Help Section -->
	<div
		class="mt-8 p-6 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg shadow-sm"
	>
		<h3 class="text-lg font-semibold text-blue-900 dark:text-blue-100 mb-3">
			What happens next?
		</h3>
		<ul class="space-y-2 text-sm text-blue-800 dark:text-blue-200">
			<li class="flex items-start space-x-2">
				<Badge variant="outline" class="mt-0.5 text-xs">1</Badge>
				<span>We'll fetch and analyze the content from your URL</span>
			</li>
			<li class="flex items-start space-x-2">
				<Badge variant="outline" class="mt-0.5 text-xs">2</Badge>
				<span>Our system will extract key information and metadata</span
				>
			</li>
			<li class="flex items-start space-x-2">
				<Badge variant="outline" class="mt-0.5 text-xs">3</Badge>
				<span
					>The content will be processed and included in the upcoming
					podcast episode</span
				>
			</li>
		</ul>
	</div>
</div>
