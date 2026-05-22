let currentUserRole = null;
let currentUserEmail = null;
let loans = [];
let allLoans = [];
let allUsers = [];

$(document).ready(function () {

    console.log("JS loaded");

    loadUser();

    $('#toggleFormBtn').click(function () {
        $('#bookId').val('');
        $('#addBookForm')[0].reset();
        $('#bookFormTitle').text('Add New Book');
        $('#saveBookBtn').text('Save Book');
        $('#addBookForm').slideToggle();
    });

    $('#addBookForm').submit(function (e) {
        e.preventDefault();

        const bookId = $('#bookId').val();
        const isUpdate = bookId !== "";

        const bookData = {
            title: $('#title').val(),
            author: $('#author').val(),
            numberOfCopies: parseInt($('#numberOfCopies').val(), 10)
        };

        if (isUpdate) {
            bookData.id = parseInt(bookId, 10);
        }

        const ajaxUrl = isUpdate ? `/books/${bookId}` : '/books';
        const ajaxType = isUpdate ? 'PUT' : 'POST';

        $.ajax({
            url: ajaxUrl,
            type: ajaxType,
            contentType: 'application/json',
            data: JSON.stringify(bookData),

            success: function () {
                alert(isUpdate ? 'Book updated successfully!' : 'Book added successfully!');
                $('#addBookForm')[0].reset();
                $('#bookId').val('');
                $('#addBookForm').slideUp();
                loadLoans();
            },

            error: function () {
                alert('Error processing book operation.');
            }
        });
    });

    $('#toggleForm2Btn').click(function () {
        $('#userId').val('');
        $('#addUserForm')[0].reset();
        $('#userFormTitle').text('Add New User');
        $('#saveUserBtn').text('Save User');
        $('#addUserForm').slideToggle();
    });

    $('#addUserForm').submit(function (e) {
        e.preventDefault();

        const userId = $('#userId').val();
        const isUpdate = userId !== "";

        const userData = {
            email: $('#email').val(),
            name: $('#name').val(),
            password: $('#password').val(),
            borrowLimit: parseInt($('#borrow_limit').val(), 10)
        };

        if (isUpdate) {
            userData.id = parseInt(userId, 10);
        }

        const ajaxUrl = isUpdate ? `/users/${userId}` : '/user/create';
        const ajaxType = isUpdate ? 'PUT' : 'POST';

        $.ajax({
            url: ajaxUrl,
            type: ajaxType,
            contentType: 'application/json',
            data: JSON.stringify(userData),

            success: function (response) {
                alert(isUpdate ? 'User updated successfully!' : 'User added successfully!');
                $('#addUserForm')[0].reset();
                $('#userId').val('');
                $('#addUserForm').slideUp();
                loadLoans();
            },

            error: function (xhr) {
                console.error(xhr);
                alert('Error processing user operation. Look at the console for details.');
            }
        });
    });
});


function loadUser() {
    $.get("/api/user/info", function (user) {
        currentUserRole = user.role;
        currentUserEmail = user.email;

        if (user.role === "ADMIN") {
            $("#adminPanel").show();
        }
        if (user.role === "READER") {
            loadRecommendations(user.id);
        }
        loadLoans();
    });
}

function loadLoans() {
    $.get("/loans/active", function (data) {
        loans = data;

        if (currentUserRole === "ADMIN") {
            renderLoansTable();

            $.get("/loans/all", function (allData) {
                allLoans = allData;
                renderAllLoansTable();
            });

            $.get("/users", function (usersData) {
                allUsers = usersData;
                renderAllUsersTable();
            });
        }
        loadBooks();
    });
}

function loadBooks() {
    $.get("/books", function (data) {
        let rows = "";
        data._embedded.bookList.forEach(book => {
            rows += renderBook(book);
        });
        $("#booksBody").html(rows);
    });
}

function getLoanForBook(bookId) {
    return loans.find(
        l => l.book.id === Number(bookId) && l.user.email === currentUserEmail
    );
}


function renderBook(book) {
    let userLoan = getLoanForBook(book.id);
    let userHasBook = !!userLoan;
    let noCopiesLeft = Number(book.numberOfCopies) === 0;

    let adminButtons = "";
    let userButtons = "";

    if (currentUserRole === "ADMIN") {
        let hasAnyLoans = loans.some(l => l.book.id === book.id);

        adminButtons = `
            <button onclick="editBook(${book.id}, '${book.title}', '${book.author}', ${book.numberOfCopies})">
                Edit
            </button>
            <button onclick="deleteBook(${book.id})"
                ${hasAnyLoans ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""}>
                Delete
            </button>
        `;
    }

    if (currentUserRole === "READER") {
        if (userHasBook) {
            userButtons += `<button onclick="returnBook(${book.id})">Return</button>`;
        } else if (!noCopiesLeft) {
            userButtons += `<button onclick="borrowBook(${book.id})">Borrow</button>`;
        }
    }

    return `
        <tr>
            <td>${book.id}</td>
            <td>${book.title}</td>
            <td>${book.author}</td>
            <td>${book.status}</td>
            <td>${book.numberOfCopies}</td>
            <td>${adminButtons}${userButtons}</td>
        </tr>
    `;
}

function renderLoansTable() {
    let rows = "";
    if (loans.length === 0) {
        rows = `<tr><td colspan="6" style="text-align:center;">No active loans at the moment.</td></tr>`;
    } else {
        loans.forEach(loan => {
            let bookTitle = loan.book ? loan.book.title : "Unknown Book";
            let userEmail = loan.user ? loan.user.email : "Unknown User";
            rows += `
                <tr>
                    <td>${loan.id}</td>
                    <td>${bookTitle}</td>
                    <td>${userEmail}</td>
                    <td>${loan.loanDate || '-'}</td>
                    <td>${loan.dueDate || '-'}</td>
                    <td><span class="status-badge">${loan.status}</span></td>
                </tr>
            `;
        });
    }
    $("#loansBody").html(rows);
}

function renderAllLoansTable() {
    let rows = "";
    if (allLoans.length === 0) {
        rows = `<tr><td colspan="7" style="text-align:center;">No active loans at the moment.</td></tr>`;
    } else {
        allLoans.forEach(loan => {
            let bookTitle = loan.book ? loan.book.title : "Unknown Book";
            let userEmail = loan.user ? loan.user.email : "Unknown User";
            rows += `
                <tr>
                    <td>${loan.id}</td>
                    <td>${bookTitle}</td>
                    <td>${userEmail}</td>
                    <td>${loan.loanDate || '-'}</td>
                    <td>${loan.dueDate || '-'}</td>
                    <td>${loan.returnDate || '-'}</td>
                    <td><span class="status-badge">${loan.status}</span></td>
                </tr>
            `;
        });
    }
    $("#loansAllBody").html(rows);
}

function renderAllUsersTable() {
    let rows = "";
    if (allUsers.length === 0) {
        rows = `<tr><td colspan="7" style="text-align:center;">No users found in database.</td></tr>`;
    } else {
        allUsers.forEach(user => {
            let userStatus = user.active ? "Active" : "Blocked";
            rows += `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.name || '-'}</td>
                    <td>${user.email}</td>
                    <td>${user.role}</td>
                    <td>${user.borrowLimit !== undefined ? user.borrowLimit : '-'}</td>
                    <td><span class="status-badge">${userStatus}</span></td>
                    <td>
                      <button onclick="editUser(${user.id}, '${user.email}', '${user.name}', '${user.password || ''}', ${user.borrowLimit || 0})">
                          Edit
                      </button>
                      <button onclick="deleteUser(${user.id})">
                            Delete
                        </button>
                         <button onclick="toggleUserStatus(${user.id}, ${user.active})">
                              ${user.active ? 'Deactivate' : 'Activate'}
                          </button>
                    </td>
                </tr>
            `;
        });
    }
    $("#usersAllBody").html(rows);
}

function editBook(id, title, author, numberOfCopies) {
    $('#bookId').val(id);
    $('#title').val(title);
    $('#author').val(author);
    $('#numberOfCopies').val(numberOfCopies);

    $('#bookFormTitle').text('Update Book (ID: ' + id + ')');
    $('#saveBookBtn').text('Update Book');
    $('#addBookForm').slideDown();
}

function editUser(id, email, name, password, borrowLimit) {
    $('#userId').val(id);
    $('#email').val(email);
    $('#name').val(name);
    $('#password').val(password);
    $('#borrow_limit').val(borrowLimit);

    $('#userFormTitle').text('Update User (ID: ' + id + ')');
    $('#saveUserBtn').text('Update User');
    $('#addUserForm').slideDown();
}

function deleteBook(id) {
    $.ajax({
        url: `/books/${id}`,
        type: "DELETE",
        success: function () { loadLoans(); },
        error: function () { alert("Cannot delete book"); }
    });
}

function borrowBook(id) {
    $.ajax({
        url: `/${id}/borrow`,
        type: "POST",
        success: function () { loadLoans(); },
        error: function () { alert("Cannot borrow book"); }
    });
}

function returnBook(id) {
    $.ajax({
        url: `/${id}/return`,
        type: "POST",
        success: function () { loadLoans(); },
        error: function () { alert("Cannot return book"); }
    });
}
function deleteUser(id) {
    if (!confirm("Are you sure you want to delete this user?")) {
        return;
    }

    $.ajax({
        url: `/users/${id}`,
        type: "DELETE",

        success: function () {
            alert("User deleted");
            loadLoans();
        },

        error: function () {
            alert("Cannot delete user");
        }
    });
}

function toggleUserStatus(id, currentStatus) {

    const newStatus = !currentStatus;

    $.ajax({
        url: `/users/${id}/status`,
        type: "PUT",
        contentType: "application/json",

        data: JSON.stringify({
            active: newStatus
        }),

        success: function () {
            alert("User status updated");
            loadLoans();
        },

        error: function () {
            alert("Cannot update user status");
        }
    });
}
function loadRecommendations(userId) {
    $.get(`/recommendations/${userId}`, function (books) {
        let listItems = "";

        if (books.length === 0) {
            listItems = "<li>We are so sorry, but there are no recommendations for you yet</li>";
        } else {
            books.forEach(title => {
                listItems += `<li style="margin-bottom: 5px; font-weight: bold;">${title}</li>`;
            });
        }

        $("#recommendationsList").html(listItems);
        $("#recommendationsPanel").show();
    }).fail(function() {
        console.error("Error with loading");
    });
}