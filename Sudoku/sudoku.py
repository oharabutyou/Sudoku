
import numpy as np
import copy

BOARD_SIZE = 4
BOARD_NUM = BOARD_SIZE**2
BOARD_INDEXSIZE = BOARD_SIZE**4
# sudoku_board = np.zeros(shape=(BOARD_INDEXSIZE), dtype=int)
sudoku_board = [
3,0,0,0,0,0,0,0,7,0,0,0,0,0,0,8,
16,0,7,11,1,9,0,0,15,14,3,10,12,0,0,6,
0,9,0,0,0,14,15,0,0,0,0,0,0,0,0,10,
0,0,14,0,0,3,11,0,13,0,9,0,16,0,2,0,
8,0,12,1,5,7,0,15,0,9,0,0,4,0,0,0,
0,16,5,0,0,0,0,0,0,13,0,0,15,0,0,0,
11,0,0,14,12,0,0,4,0,2,7,0,0,0,9,5,
0,0,13,0,0,0,8,0,1,0,0,0,10,11,6,0,
0,6,16,4,8,0,0,0,0,0,0,0,0,2,0,0,
14,0,0,3,0,10,0,5,0,0,2,11,0,0,0,4,
2,0,0,8,16,0,14,0,6,0,0,0,0,0,0,12,
0,0,0,0,0,0,0,0,12,3,0,0,11,10,7,0,
9,0,0,0,15,5,0,7,0,4,0,1,3,13,16,0,
13,0,0,0,0,4,0,0,0,5,14,0,0,0,0,0,
12,0,6,15,2,0,13,10,0,7,0,8,1,4,0,9,
0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,7
    ]

def load():
    pass


def addNum(index):
    global sudoku_board
    # if all index passed, board is solved and return true
    if index >= BOARD_INDEXSIZE:
        return True
    # skip finding loaded number
    if sudoku_board[index] != 0:
        return addNum(index+1)
    # print(cpy)
    # try to put num
    for num in range(1, BOARD_NUM+1):
        sudoku_board[index] = num
        if check(index) and addNum(index+1):
            # break chain when solution is found
            return True
    # return false when no solution found
    sudoku_board[index] = 0
    return False


def getColMember(index):
    return range(index % BOARD_NUM, BOARD_INDEXSIZE, BOARD_NUM)


def getRowMember(index):
    return range(index - index % BOARD_NUM, index - index % BOARD_NUM + BOARD_NUM)


def getMassMember(index):
    member = []
    massCol = int(index % BOARD_NUM / BOARD_SIZE)
    massRow = int(index / BOARD_NUM / BOARD_SIZE)
    leftUp = massCol * BOARD_SIZE + massRow * BOARD_SIZE * BOARD_NUM
    for row in range(BOARD_SIZE):
        member.extend(range(leftUp + row * BOARD_NUM, leftUp + row * BOARD_NUM + BOARD_SIZE))
    return member


def check(index):
    check_sum = 0
    # check row
    for row in getRowMember(index):
        check_sum += (row != index) * (sudoku_board[row] == sudoku_board[index])
    # check col
    for col in getColMember(index):
        check_sum += (col != index) * (sudoku_board[col] == sudoku_board[index])
    # check mass
    for mass in getMassMember(index):
        check_sum += (mass != index) * (sudoku_board[mass] == sudoku_board[index])
    # throughing all check means ok
    return check_sum == 0

def printBoard(board):
    string = ""
    for row in range(BOARD_NUM):
        for col in range(BOARD_NUM):
            string += (str(board[row*BOARD_NUM+col])+",")
        string += "\n"
    print(string)


if __name__ == "__main__":
    print(addNum(0))
    printBoard(sudoku_board)
