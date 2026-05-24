package io.github.jamalam360.sort_it_out.client.worker;

import io.github.jamalam360.sort_it_out.SortItOut;
import net.minecraft.client.Minecraft;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientSortWorker {
	public static final ClientSortWorker INSTANCE = new ClientSortWorker();
	private final ConcurrentLinkedQueue<ClickAction> queue;
	private final ScheduledExecutorService scheduler;
	private final AtomicBoolean producerActive;
	private volatile Minecraft minecraft;
	private ScheduledFuture<?> scheduledTask;

	private ClientSortWorker() {
		this.queue = new ConcurrentLinkedQueue<>();
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.producerActive = new AtomicBoolean(false);
	}

	public void start(Minecraft minecraft, long pollIntervalMs) {
		this.minecraft = minecraft;
		this.producerActive.set(true);
		this.queue.clear();
		scheduledTask = scheduler.scheduleAtFixedRate(
				this::poll,
				0,
				pollIntervalMs,
				TimeUnit.MILLISECONDS
		);
	}

	public void complete() {
		this.producerActive.set(false);
	}

	private void stop() {
		if (this.producerActive.get()) {
			SortItOut.LOGGER.warn("Worker terminated while producer is still active");
		}

		if (this.scheduledTask != null) {
			scheduledTask.cancel(false);
		}

		this.queue.clear();
	}

	public void push(ClickAction action) {
		System.out.println(action);
		queue.offer(action);
	}

	public boolean isWorking() {
		return this.producerActive.get() || !this.queue.isEmpty();
	}

	private void poll() {
		ClickAction action = queue.poll();
		if (action == null) {
			if (!this.producerActive.get()) {
				this.stop();
			}

			return;
		}

		if (!action.execute(this.minecraft)) {
			SortItOut.LOGGER.info("Aborting sort because an action failed to complete successfully");
			this.stop();
		}
	}
}
