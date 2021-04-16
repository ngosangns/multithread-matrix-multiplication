import matplotlib
import matplotlib.pyplot as plt
import numpy as np

f = open("experiments_low.txt","r")

data = f.read().split("\n")
data = [x.split(" ") for x in data]
data = np.array(data).transpose()

labels = data[0]
single = data[1].astype(np.int32)
multi = data[2].astype(np.int32)

x = np.arange(len(labels))  # the label locations
width = 0.35  # the width of the bars

fig, ax = plt.subplots()
rects1 = ax.bar(x - width/2, single, width, label='Single-thread')
rects2 = ax.bar(x + width/2, multi, width, label='Multi-thread')

# Add some text for labels, title and custom x-axis tick labels, etc.
ax.set_ylabel('Time (ms)')
ax.set_title('Performance (low size matrices)')
ax.set_xticks(x)
ax.set_xticklabels(labels)
ax.legend()

ax.bar_label(rects1, padding=3)
ax.bar_label(rects2, padding=3)

fig.tight_layout()

plt.show()