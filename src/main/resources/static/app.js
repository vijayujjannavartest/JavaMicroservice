const api = '/api/issues';

const issueListEl = document.getElementById('issueList');
const issueViewEl = document.getElementById('issueView');
const modalEl = document.getElementById('issueModal');
const formEl = document.getElementById('issueForm');

let issues = [];
let selectedIssue = null;

async function fetchIssues() {
    const res = await fetch(api);
    issues = await res.json();
    renderIssueList();
    if (!selectedIssue && issues.length) {
        openIssue(issues[0].id);
    }
}

function renderIssueList() {
    issueListEl.innerHTML = '';
    issues.forEach(issue => {
        const li = document.createElement('li');
        li.innerHTML = `<strong>${issue.id}</strong><br>${issue.summary}`;
        li.onclick = () => openIssue(issue.id);
        issueListEl.appendChild(li);
    });
}

async function openIssue(id) {
    const res = await fetch(`${api}/${id}`);
    selectedIssue = await res.json();
    renderIssueView();
}

function renderIssueView() {
    if (!selectedIssue) {
        issueViewEl.innerHTML = '<div class="placeholder">Select an issue from the left panel.</div>';
        return;
    }

    const labelChips = (selectedIssue.labels || []).map(l => `<span class="chip">${l}</span>`).join('');
    const watcherChips = (selectedIssue.watchers || []).map(w => `<span class="chip">${w}</span>`).join('');

    issueViewEl.innerHTML = `
      <article class="issue-card">
        <div class="issue-header">
          <div>
            <div class="issue-key">${selectedIssue.id}</div>
            <h1 class="issue-title">${selectedIssue.summary}</h1>
          </div>
          <div class="issue-actions" id="actionWrap">
            <button class="dots-btn" id="dotsBtn">⋯</button>
            <div class="dots-menu">
              <button id="editBtn">Edit issue</button>
              <button id="deleteBtn">Delete issue</button>
            </div>
          </div>
        </div>

        <p>${selectedIssue.description || 'No description provided.'}</p>

        <div class="meta-grid">
          <div class="meta-item"><strong>Status</strong>${selectedIssue.status}</div>
          <div class="meta-item"><strong>Priority</strong>${selectedIssue.priority}</div>
          <div class="meta-item"><strong>Assignee</strong>${selectedIssue.assignee}</div>
          <div class="meta-item"><strong>Reporter</strong>${selectedIssue.reporter}</div>
          <div class="meta-item"><strong>Labels</strong>${labelChips || '-'}</div>
          <div class="meta-item"><strong>Updated</strong>${new Date(selectedIssue.updatedAt).toLocaleString()}</div>
        </div>

        <section class="watcher-area">
          <strong>Watchers</strong>
          <div>${watcherChips || 'No watchers yet.'}</div>
          <form id="watcherForm">
            <input id="watcherInput" placeholder="Add watcher name" required>
            <button class="primary" type="submit">Add watcher</button>
          </form>
          <button id="notificationBtn">Check my notifications</button>
          <div id="notificationArea"></div>
        </section>
      </article>
    `;

    const actionWrap = document.getElementById('actionWrap');
    document.getElementById('dotsBtn').onclick = () => actionWrap.classList.toggle('open');
    document.getElementById('editBtn').onclick = () => openModal(selectedIssue);
    document.getElementById('deleteBtn').onclick = deleteSelectedIssue;
    document.getElementById('watcherForm').onsubmit = addWatcher;
    document.getElementById('notificationBtn').onclick = getNotifications;
}

function openModal(issue) {
    modalEl.classList.remove('hidden');
    document.getElementById('modalTitle').textContent = issue ? 'Edit Issue' : 'Create Issue';

    document.getElementById('issueId').value = issue?.id || '';
    document.getElementById('summary').value = issue?.summary || '';
    document.getElementById('description').value = issue?.description || '';
    document.getElementById('status').value = issue?.status || 'To Do';
    document.getElementById('priority').value = issue?.priority || 'Medium';
    document.getElementById('assignee').value = issue?.assignee || '';
    document.getElementById('reporter').value = issue?.reporter || '';
    document.getElementById('labels').value = (issue?.labels || []).join(', ');
    document.getElementById('watchers').value = (issue?.watchers || []).join(', ');
}

function closeModal() {
    modalEl.classList.add('hidden');
    formEl.reset();
}

function parseCsv(value) {
    return value.split(',').map(v => v.trim()).filter(Boolean);
}

formEl.onsubmit = async (e) => {
    e.preventDefault();
    const id = document.getElementById('issueId').value;

    const payload = {
        summary: document.getElementById('summary').value,
        description: document.getElementById('description').value,
        status: document.getElementById('status').value,
        priority: document.getElementById('priority').value,
        assignee: document.getElementById('assignee').value,
        reporter: document.getElementById('reporter').value,
        labels: parseCsv(document.getElementById('labels').value),
        watchers: parseCsv(document.getElementById('watchers').value)
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `${api}/${id}` : api;

    await fetch(url, {
        method,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    });

    closeModal();
    await fetchIssues();
    if (id) await openIssue(id);
};

async function addWatcher(e) {
    e.preventDefault();
    const watcher = document.getElementById('watcherInput').value;
    await fetch(`${api}/${selectedIssue.id}/watchers`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({watcher})
    });
    await openIssue(selectedIssue.id);
}

async function deleteSelectedIssue() {
    await fetch(`${api}/${selectedIssue.id}`, {method: 'DELETE'});
    selectedIssue = null;
    await fetchIssues();
    renderIssueView();
}

async function getNotifications() {
    const user = prompt('Enter watcher name to fetch notifications');
    if (!user) return;

    const res = await fetch(`${api}/notifications/${encodeURIComponent(user)}`);
    const notifications = await res.json();
    const html = notifications.length
        ? notifications.map(n => `<div class="notice">${n.message} at ${new Date(n.timestamp).toLocaleString()}</div>`).join('')
        : '<div class="notice">No notifications yet.</div>';
    document.getElementById('notificationArea').innerHTML = html;
}

document.getElementById('createIssueBtn').onclick = () => openModal();
document.getElementById('cancelBtn').onclick = closeModal;

fetchIssues();
