import matplotlib.pyplot as plt

times = [433, 222, 140, 105, 95, 86, 86, 82]
labels = ['Seq (1)', '2 Thr', '4 Thr', '8 Thr', '12 Thr', '16 Thr', '22 Thr', 'Stream']
x_pos = range(len(labels))

plt.figure(figsize=(10, 6))

plt.plot(x_pos, times, marker='o', linestyle='-', color='#2b7bba', linewidth=2, markersize=8, label='Execution Time')
plt.bar(x_pos, times, color='#a6cee3', alpha=0.5)

plt.title('Performance Analysis: Sequential vs Multi-Threaded vs Stream', fontsize=14)
plt.ylabel('Time (ms)', fontsize=12)
plt.xlabel('Configuration', fontsize=12)

plt.xticks(x_pos, labels)

for i, v in enumerate(times):
    plt.text(i, v + 10, str(v), ha='center', fontweight='bold')

plt.grid(True, linestyle='--', alpha=0.7)

plt.tight_layout()
plt.show()